package Library;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField textFieldUsername;
    private JPasswordField passwordField;
    private JButton buttonLogin;

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel labelUsername = new JLabel("Username:");
        JLabel labelPassword = new JLabel("Password:");
        textFieldUsername = new JTextField();
        passwordField = new JPasswordField();
        buttonLogin = new JButton("Login");

        labelUsername.setBounds(30, 30, 80, 25);
        textFieldUsername.setBounds(120, 30, 150, 25);
        labelPassword.setBounds(30, 70, 80, 25);
        passwordField.setBounds(120, 70, 150, 25);
        buttonLogin.setBounds(100, 110, 100, 30);

        add(labelUsername);
        add(textFieldUsername);
        add(labelPassword);
        add(passwordField);
        add(buttonLogin);

        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateLogin();
            }
        });
    }

    private void validateLogin() {
        String username = textFieldUsername.getText();
        String password = new String(passwordField.getPassword());

        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check if user exists
            if (resultSet.next()) {
                // Successful login
                new LibraryManagementMain().setVisible(true); // Open main application
                dispose(); // Close login frame
            } else {
                // Invalid credentials
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

            // Close resources
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}