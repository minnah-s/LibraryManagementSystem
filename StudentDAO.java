package Library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudentDAO {

    // Generate next available student ID in format STU001, STU002, ...
    public static String generateStudentId() {
        String nextId = "STU001";

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT student_id FROM students ORDER BY student_id DESC LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String lastId = resultSet.getString("student_id");
                int number = Integer.parseInt(lastId.substring(3)) + 1;
                nextId = String.format("STU%03d", number);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nextId;
    }

    // Insert a student into the database
    public static boolean registerStudent(Student student) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO students (student_id, student_name) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, student.getStudentId());
            preparedStatement.setString(2, student.getName());

            int rowsInserted = preparedStatement.executeUpdate();
            preparedStatement.close();

            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
