package Library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IssueBookDAO {

    // Issue a book copy to a student
    public static boolean issueBook(String copyId, Student student) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Insert into issued_books
            String insertSQL = "INSERT INTO issued_books (book_id, student_id, copy_id) " +
                               "SELECT book_id, ?, ? FROM book_copies WHERE copy_id = ?";
            PreparedStatement insertStmt = connection.prepareStatement(insertSQL);
            insertStmt.setInt(1, Integer.parseInt(student.getStudentId())); // assuming numeric ID
            insertStmt.setString(2, copyId);
            insertStmt.setString(3, copyId);
            int result = insertStmt.executeUpdate();
            insertStmt.close();

            // Update book_copies status to 'issued'
            if (result > 0) {
                String updateSQL = "UPDATE book_copies SET status = 'issued' WHERE copy_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSQL);
                updateStmt.setString(1, copyId);
                updateStmt.executeUpdate();
                updateStmt.close();

                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Return a book
    public static boolean returnBook(String copyId) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Delete from issued_books
            PreparedStatement deleteStmt = connection.prepareStatement(
                "DELETE FROM issued_books WHERE copy_id = ?");
            deleteStmt.setString(1, copyId);
            int deleted = deleteStmt.executeUpdate();

            // Update book copy status to available
            if (deleted > 0) {
                PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE book_copies SET status = 'available' WHERE copy_id = ?");
                updateStmt.setString(1, copyId);
                updateStmt.executeUpdate();
                updateStmt.close();

                connection.commit();
                return true;
            } else {
                connection.rollback();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
