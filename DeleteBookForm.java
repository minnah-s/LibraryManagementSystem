package Library;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeleteBookForm extends JFrame {
    private JComboBox<String> comboBoxTitles;
    private JButton buttonDelete;
    private JButton buttonCancel;

    public DeleteBookForm() {
        setTitle("Delete Book");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Create components
        JLabel labelSelectTitle = new JLabel("Select Book to Delete:");
        labelSelectTitle.setBounds(30, 30, 150, 25);
        add(labelSelectTitle);

        comboBoxTitles = new JComboBox<>();
        comboBoxTitles.setBounds(180, 30, 180, 25);
        add(comboBoxTitles);

        buttonDelete = new JButton("Delete");
        buttonDelete.setBounds(100, 80, 90, 30);
        add(buttonDelete);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setBounds(200, 80, 90, 30);
        add(buttonCancel);

        // Add action listeners
        buttonDelete.addActionListener(e -> deleteBook());
        buttonCancel.addActionListener(e -> dispose());

        // Load book titles
        loadBookTitles();
    }

    private void loadBookTitles() {
        comboBoxTitles.removeAllItems();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT title FROM books";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                comboBoxTitles.addItem(rs.getString("title"));
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading book titles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        String selectedBook = (String) comboBoxTitles.getSelectedItem();
        if (selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this book?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (BookDAO.deleteBook(selectedBook)) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully");
                loadBookTitles();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete book. It may have issued copies.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DeleteBookForm form = new DeleteBookForm();
            form.setVisible(true);
        });
    }
}