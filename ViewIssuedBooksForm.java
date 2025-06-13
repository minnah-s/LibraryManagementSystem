package Library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewIssuedBooksForm extends JFrame {

    private JTable tableIssuedBooks;
    private DefaultTableModel tableModel;
    private JButton buttonReturnBook;

    public ViewIssuedBooksForm() {
        setTitle("View Issued Books");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create table model and table with an additional column for copy ID
        tableModel = new DefaultTableModel(new String[]{"Student Name", "Student ID", "Book Title", "Issue Date", "Copy ID"}, 0);
        tableIssuedBooks = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableIssuedBooks);
        add(scrollPane, BorderLayout.CENTER);

        // Create return book button
        buttonReturnBook = new JButton("Return Book");
        buttonReturnBook.addActionListener(e -> returnBook());
        add(buttonReturnBook, BorderLayout.SOUTH);

        // Load issued books from the database
        loadIssuedBooks();
    }

    public void loadIssuedBooks() {
        // Clear the existing rows in the table
        tableModel.setRowCount(0);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT ib.student_name, ib.student_id, b.title, ib.issue_date, ib.copy_id " +
                        "FROM issued_books ib " +
                        "JOIN book_copies bc ON ib.copy_id = bc.copy_id " +
                        "JOIN books b ON bc.book_id = b.id";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Populate the table with issued book data
            while (resultSet.next()) {
                String studentName = resultSet.getString("student_name");
                String studentId = resultSet.getString("student_id");
                String bookTitle = resultSet.getString("title");
                String issueDate = resultSet.getString("issue_date");
                String copyId = resultSet.getString("copy_id");
                tableModel.addRow(new Object[]{studentName, studentId, bookTitle, issueDate, copyId});
            }

            // Close resources
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage());
        }
    }

    private void returnBook() {
        int selectedRow = tableIssuedBooks.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to return.");
            return;
        }

        String copyId = (String) tableModel.getValueAt(selectedRow, 4);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to return the book?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = IssueBookDAO.returnBook(copyId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Book returned successfully.");
            loadIssuedBooks(); // refresh table
        } else {
            JOptionPane.showMessageDialog(this, "Failed to return the book.");
        }
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewIssuedBooksForm form = new ViewIssuedBooksForm();
            form.setVisible(true);
        });
    }
}