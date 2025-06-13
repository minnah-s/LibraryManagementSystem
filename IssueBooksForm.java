package Library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IssueBooksForm extends JFrame {

    private JTable tableBooks;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboBoxStudentId;
    private JTextField textFieldStudentName;

    public IssueBooksForm() {
        setTitle("Issue Books");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table for books
        tableModel = new DefaultTableModel(new String[]{"Copy ID", "Title", "Author", "ISBN", "Status"}, 0);
        tableBooks = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableBooks);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for student info at top
        JPanel panelStudentDetails = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboBoxStudentId = new JComboBox<>();
        loadStudentIds();
        comboBoxStudentId.setPreferredSize(new Dimension(150, 25));
        panelStudentDetails.add(new JLabel("Student ID:"));
        panelStudentDetails.add(comboBoxStudentId);

        textFieldStudentName = new JTextField(15);
        panelStudentDetails.add(new JLabel("Student Name:"));
        panelStudentDetails.add(textFieldStudentName);

        JButton buttonLoadName = new JButton("Load Name");
        panelStudentDetails.add(buttonLoadName);
        add(panelStudentDetails, BorderLayout.NORTH);

        buttonLoadName.addActionListener(e -> {
            String selectedStudentId = (String) comboBoxStudentId.getSelectedItem();
            if (selectedStudentId != null && !selectedStudentId.trim().isEmpty()) {
                loadStudentName(selectedStudentId);
            }
        });

        // Panel for Issue Button at bottom
        JPanel panelButtons = new JPanel(new FlowLayout());
        JButton buttonIssueBook = new JButton("Issue Book");
        panelButtons.add(buttonIssueBook);
        add(panelButtons, BorderLayout.SOUTH);

        buttonIssueBook.addActionListener(e -> issueBook());

        loadBooks();
    }

    public void loadBooks() {
        tableModel.setRowCount(0);

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT bc.copy_id, b.title, b.author, b.isbn, bc.status " +
                        "FROM book_copies bc " +
                        "JOIN books b ON bc.book_id = b.id " +
                        "WHERE bc.status = 'available'";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String copyId = resultSet.getString("copy_id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String isbn = resultSet.getString("isbn");
                String status = resultSet.getString("status");
                tableModel.addRow(new Object[]{copyId, title, author, isbn, status});
            }

            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage());
        }
    }

    private void loadStudentIds() {
        comboBoxStudentId.removeAllItems();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT student_id FROM students";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                comboBoxStudentId.addItem(resultSet.getString("student_id"));
            }

            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student IDs: " + e.getMessage());
        }
    }

    private void loadStudentName(String studentId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT student_name FROM students WHERE student_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, studentId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String studentName = resultSet.getString("student_name");
                textFieldStudentName.setText(studentName);
            } else {
                textFieldStudentName.setText("");
                JOptionPane.showMessageDialog(this, "Student not found for the selected ID.");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student name: " + e.getMessage());
        }
    }

    private void issueBook() {
        int selectedRow = tableBooks.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to issue.");
            return;
        }

        String copyId = (String) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String status = (String) tableModel.getValueAt(selectedRow, 4);

        if (!"available".equals(status)) {
            JOptionPane.showMessageDialog(this, "Selected book is not available.");
            return;
        }

        String studentId = (String) comboBoxStudentId.getSelectedItem();
        String studentName = textFieldStudentName.getText().trim();

        if (studentId == null || studentId.trim().isEmpty() || studentName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student ID and enter the student name.");
            return;
        }

        issueBookInDatabase(copyId, bookTitle, studentName, studentId);
    }

    private void issueBookInDatabase(String copyId, String bookTitle, String studentName, String studentId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Update book copy status
                String updateSql = "UPDATE book_copies SET status = 'issued' WHERE copy_id = ? AND status = 'available'";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, copyId);
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    // Log the issue
                    String logSql = "INSERT INTO issued_books (copy_id, student_name, student_id) VALUES (?, ?, ?)";
                    PreparedStatement logStatement = connection.prepareStatement(logSql);
                    logStatement.setString(1, copyId);
                    logStatement.setString(2, studentName);
                    logStatement.setString(3, studentId);
                    logStatement.executeUpdate();
                    logStatement.close();

                    connection.commit();
                    JOptionPane.showMessageDialog(this, "Book '" + bookTitle + "' issued successfully!");
                    loadBooks(); // Refresh books list after issuing
                } else {
                    connection.rollback();
                    JOptionPane.showMessageDialog(this, "Failed to issue the book: Book is no longer available.");
                }

                updateStatement.close();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage());
        }
    }
}