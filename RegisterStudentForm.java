package Library;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterStudentForm extends JFrame {

    private JTextField textFieldStudentName;
    private JLabel labelGeneratedId;

    public RegisterStudentForm() {
        setTitle("Register New Student");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2)); // 3 rows, 2 columns

        JLabel labelStudentId = new JLabel("Student ID:");
        labelGeneratedId = new JLabel("Will be generated");
        labelGeneratedId.setForeground(Color.BLUE);

        JLabel labelStudentName = new JLabel("Student Name:");
        textFieldStudentName = new JTextField();

        JButton buttonRegister = new JButton("Register");

        add(labelStudentId);
        add(labelGeneratedId);
        add(labelStudentName);
        add(textFieldStudentName);
        add(new JLabel()); // Empty cell for spacing
        add(buttonRegister);

        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerStudent();
            }
        });
    }

    private String generateStudentId() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Get the last used student ID
            String sql = "SELECT student_id FROM students ORDER BY student_id DESC LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            int nextNumber = 1; // Default if no students exist
            if (resultSet.next()) {
                String lastId = resultSet.getString("student_id");
                if (lastId != null && lastId.startsWith("STU")) {
                    try {
                        nextNumber = Integer.parseInt(lastId.substring(3)) + 1;
                    } catch (NumberFormatException e) {
                        // If parsing fails, start from 1
                        nextNumber = 1;
                    }
                }
            }

            resultSet.close();
            preparedStatement.close();

            // Format the new ID with leading zeros
            return String.format("STU%03d", nextNumber);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating student ID: " + e.getMessage());
            return null;
        }
    }
    
    private void registerStudent() {
        String studentName = textFieldStudentName.getText().trim();

        if (studentName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the student name.");
            return;
        }

        String studentId = StudentDAO.generateStudentId();
        Student student = new Student(studentName, studentId);

        boolean success = StudentDAO.registerStudent(student);
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Student registered successfully!\nStudent ID: " + studentId);
            textFieldStudentName.setText("");
            labelGeneratedId.setText("Will be generated");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register student.");
        }
    }


    
}