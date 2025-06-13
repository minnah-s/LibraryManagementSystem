package Library;

import java.sql.*;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class BookDAO {

	public static boolean addBook(Book book) {
	    Connection connection = null;

	    try {
	        connection = DatabaseConnection.getConnection();
	        connection.setAutoCommit(false); // Begin transaction

	        // 1. Insert into books table
	        String bookSql = "INSERT INTO books (title, author, isbn, publisher, quantity, available_quantity) VALUES (?, ?, ?, ?, ?, ?)";
	        PreparedStatement bookStmt = connection.prepareStatement(bookSql, Statement.RETURN_GENERATED_KEYS);
	        bookStmt.setString(1, book.getTitle());
	        bookStmt.setString(2, book.getAuthor());
	        bookStmt.setString(3, book.getIsbn());
	        bookStmt.setString(4, book.getPublisher());
	        bookStmt.setInt(5, book.getQuantity());
	        bookStmt.setInt(6, book.getQuantity());
	        bookStmt.executeUpdate();

	        // 2. Get generated book ID
	        ResultSet keys = bookStmt.getGeneratedKeys();
	        if (!keys.next()) throw new SQLException("Failed to get book ID");
	        int bookId = keys.getInt(1);

	        // 3. Insert into book_copies
	        String copySql = "INSERT INTO book_copies (copy_id, book_id, status) VALUES (?, ?, 'available')";
	        PreparedStatement copyStmt = connection.prepareStatement(copySql);

	        for (int i = 0; i < book.getQuantity(); i++) {
	            String copyId = UUID.randomUUID().toString();
	            System.out.println("Copy ID: " + copyId);  // DEBUG
	            copyStmt.setString(1, copyId);
	            copyStmt.setInt(2, bookId);
	            copyStmt.addBatch();
	        }

	        copyStmt.executeBatch();
	        connection.commit();
	        return true;

	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            if (connection != null) connection.rollback();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        return false;
	    }
	}

	public static boolean deleteBook(String title) {
	    Connection connection = null;
	    try {
	        connection = DatabaseConnection.getConnection();
	        connection.setAutoCommit(false);

	        // Check if any copies are currently issued
	        String checkSql = "SELECT COUNT(*) as issued_count FROM book_copies bc " +
	                        "JOIN books b ON bc.book_id = b.id " +
	                        "WHERE b.title = ? AND bc.status = 'issued'";
	        PreparedStatement checkStatement = connection.prepareStatement(checkSql);
	        checkStatement.setString(1, title);
	        ResultSet rs = checkStatement.executeQuery();
	        
	        if (rs.next() && rs.getInt("issued_count") > 0) {
	            connection.rollback();
	            return false;
	        }
	        rs.close();
	        checkStatement.close();

	        // First delete all copies
	        String deleteCopiesSql = "DELETE bc FROM book_copies bc " +
	                               "JOIN books b ON bc.book_id = b.id " +
	                               "WHERE b.title = ?";
	        PreparedStatement deleteCopiesStatement = connection.prepareStatement(deleteCopiesSql);
	        deleteCopiesStatement.setString(1, title);
	        deleteCopiesStatement.executeUpdate();
	        deleteCopiesStatement.close();

	        // Then delete the book
	        String deleteBookSql = "DELETE FROM books WHERE title = ?";
	        PreparedStatement deleteBookStatement = connection.prepareStatement(deleteBookSql);
	        deleteBookStatement.setString(1, title);
	        int deleted = deleteBookStatement.executeUpdate();
	        deleteBookStatement.close();

	        if (deleted > 0) {
	            connection.commit();
	            return true;
	        } else {
	            connection.rollback();
	            return false;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            if (connection != null) connection.rollback();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        return false;
	    }
	}

	public static boolean updateBook(Book book) {
	    Connection connection = null;
	    try {
	        connection = DatabaseConnection.getConnection();
	        connection.setAutoCommit(false);

	        // Get current book details
	        String getCurrentSql = "SELECT b.id, COUNT(bc.copy_id) as current_copies " +
	                             "FROM books b " +
	                             "LEFT JOIN book_copies bc ON b.id = bc.book_id " +
	                             "WHERE b.title = ? " +
	                             "GROUP BY b.id";
	        PreparedStatement getCurrentStatement = connection.prepareStatement(getCurrentSql);
	        getCurrentStatement.setString(1, book.getTitle());
	        ResultSet rs = getCurrentStatement.executeQuery();

	        if (!rs.next()) {
	            connection.rollback();
	            return false;
	        }

	        int bookId = rs.getInt("id");
	        int currentCopies = rs.getInt("current_copies");
	        rs.close();
	        getCurrentStatement.close();

	        // Update book details
	        String updateSql = "UPDATE books SET author = ?, isbn = ? WHERE title = ?";
	        PreparedStatement updateStatement = connection.prepareStatement(updateSql);
	        updateStatement.setString(1, book.getAuthor());
	        updateStatement.setString(2, book.getIsbn());
	        updateStatement.setString(3, book.getTitle());
	        updateStatement.executeUpdate();
	        updateStatement.close();

	        // Handle quantity changes
	        if (book.getQuantity() > currentCopies) {
	            // Add new copies
	            String addCopiesSql = "INSERT INTO book_copies (book_id, copy_id, status) VALUES (?, ?, 'available')";
	            PreparedStatement addCopiesStatement = connection.prepareStatement(addCopiesSql);
	            for (int i = 0; i < (book.getQuantity() - currentCopies); i++) {
	                String copyId = java.util.UUID.randomUUID().toString();
	                addCopiesStatement.setInt(1, bookId);
	                addCopiesStatement.setString(2, copyId);
	                addCopiesStatement.executeUpdate();
	            }
	            addCopiesStatement.close();
	        } else if (book.getQuantity() < currentCopies) {
	            // Remove excess copies (only available ones)
	            String removeCopiesSql = "DELETE FROM book_copies " +
	                                   "WHERE book_id = ? AND status = 'available' " +
	                                   "LIMIT ?";
	            PreparedStatement removeCopiesStatement = connection.prepareStatement(removeCopiesSql);
	            removeCopiesStatement.setInt(1, bookId);
	            removeCopiesStatement.setInt(2, currentCopies - book.getQuantity());
	            removeCopiesStatement.executeUpdate();
	            removeCopiesStatement.close();
	        }

	        connection.commit();
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        try {
	            if (connection != null) connection.rollback();
	        } catch (SQLException se) {
	            se.printStackTrace();
	        }
	        return false;
	    }
	}

	public static List<Book> searchBooks(String query) {
	    List<Book> books = new ArrayList<>();
	    try (Connection connection = DatabaseConnection.getConnection()) {
	        String sql = "SELECT b.title, b.author, b.isbn, b.publisher, " +
	                    "COUNT(bc.copy_id) as total_copies, " +
	                    "SUM(CASE WHEN bc.status = 'available' THEN 1 ELSE 0 END) as available_copies " +
	                    "FROM books b " +
	                    "LEFT JOIN book_copies bc ON b.id = bc.book_id " +
	                    "WHERE b.title LIKE ? " +
	                    "GROUP BY b.id, b.title, b.author, b.isbn, b.publisher";
	        
	        PreparedStatement preparedStatement = connection.prepareStatement(sql);
	        preparedStatement.setString(1, "%" + query + "%");
	        ResultSet resultSet = preparedStatement.executeQuery();

	        while (resultSet.next()) {
	            String title = resultSet.getString("title");
	            String author = resultSet.getString("author");
	            String isbn = resultSet.getString("isbn");
	            String publisher = resultSet.getString("publisher");
	            int totalCopies = resultSet.getInt("total_copies");
	            int availableCopies = resultSet.getInt("available_copies");
	            
	            Book book = new Book(title, author, isbn, publisher, totalCopies);
	            books.add(book);
	        }

	        resultSet.close();
	        preparedStatement.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return books;
	}

}

