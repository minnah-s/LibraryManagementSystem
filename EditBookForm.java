package Library;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditBookForm extends BookFormBase {

    public EditBookForm() {
        super("Edit Book Details");
        loadBookTitles();
        // Hide the Delete button since deletion is handled separately
        buttonDelete.setVisible(false);
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

    @Override
    protected void loadBook() {
        String selectedTitle = (String) comboBoxTitles.getSelectedItem();
        if (selectedTitle == null) {
            clearFields();
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT b.*, " +
                        "COUNT(bc.copy_id) as total_copies, " +
                        "SUM(CASE WHEN bc.status = 'available' THEN 1 ELSE 0 END) as available_copies " +
                        "FROM books b " +
                        "LEFT JOIN book_copies bc ON b.id = bc.book_id " +
                        "WHERE b.title = ? " +
                        "GROUP BY b.id, b.title, b.author, b.isbn";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, selectedTitle);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                textFieldTitle.setText(rs.getString("title"));
                textFieldAuthor.setText(rs.getString("author"));
                textFieldISBN.setText(rs.getString("isbn"));
                textFieldQuantity.setText(String.valueOf(rs.getInt("total_copies")));
            } else {
                JOptionPane.showMessageDialog(this, "Book not found.");
                clearFields();
            }
            rs.close();
            statement.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading book details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void saveBook() {
        String title = textFieldTitle.getText().trim();
        String author = textFieldAuthor.getText().trim();
        String isbn = textFieldISBN.getText().trim();
        String quantityStr = textFieldQuantity.getText().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        int newQuantity;
        try {
            newQuantity = Integer.parseInt(quantityStr);
            if (newQuantity < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a non-negative integer.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                // Get current book details
                String getCurrentSql = "SELECT b.id, COUNT(bc.copy_id) as current_copies " +
                                     "FROM books b " +
                                     "LEFT JOIN book_copies bc ON b.id = bc.book_id " +
                                     "WHERE b.title = ? " +
                                     "GROUP BY b.id";
                PreparedStatement getCurrentStatement = connection.prepareStatement(getCurrentSql);
                getCurrentStatement.setString(1, title);
                ResultSet rs = getCurrentStatement.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Book not found.");
                    connection.rollback();
                    return;
                }

                int bookId = rs.getInt("id");
                int currentCopies = rs.getInt("current_copies");
                rs.close();
                getCurrentStatement.close();

                // Update book details
                String updateSql = "UPDATE books SET author = ?, isbn = ? WHERE title = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, author);
                updateStatement.setString(2, isbn);
                updateStatement.setString(3, title);
                updateStatement.executeUpdate();
                updateStatement.close();

                // Handle quantity changes
                if (newQuantity > currentCopies) {
                    // Add new copies
                    String addCopiesSql = "INSERT INTO book_copies (book_id, copy_id, status) VALUES (?, ?, 'available')";
                    PreparedStatement addCopiesStatement = connection.prepareStatement(addCopiesSql);
                    for (int i = 0; i < (newQuantity - currentCopies); i++) {
                        String copyId = java.util.UUID.randomUUID().toString();
                        addCopiesStatement.setInt(1, bookId);
                        addCopiesStatement.setString(2, copyId);
                        addCopiesStatement.executeUpdate();
                    }
                    addCopiesStatement.close();
                } else if (newQuantity < currentCopies) {
                    // Remove excess copies (only available ones)
                    String removeCopiesSql = "DELETE FROM book_copies " +
                                           "WHERE book_id = ? AND status = 'available' " +
                                           "LIMIT ?";
                    PreparedStatement removeCopiesStatement = connection.prepareStatement(removeCopiesSql);
                    removeCopiesStatement.setInt(1, bookId);
                    removeCopiesStatement.setInt(2, currentCopies - newQuantity);
                    removeCopiesStatement.executeUpdate();
                    removeCopiesStatement.close();
                }

                connection.commit();
                JOptionPane.showMessageDialog(this, "Book details updated successfully.");
                loadBookTitles();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving book details: " + e.getMessage());
        }
    }

    @Override
    protected void deleteBook() {
        // This method is no longer used as deletion is handled by DeleteBookForm
    }
}

