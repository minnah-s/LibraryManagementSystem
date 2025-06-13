package Library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibraryManagementMain extends JFrame {

    public LibraryManagementMain() {
        setTitle("Library Management System");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(10, 1));

        JButton buttonAddBooks = new JButton("Add Books");
        JButton buttonIssueBooks = new JButton("Issue Books");
        JButton buttonViewIssuedBooks = new JButton("View Issued Books");
        JButton buttonRegisterStudent = new JButton("Register Student");
        JButton buttonEditBooks = new JButton("Edit Books");
        JButton buttonDeleteBooks = new JButton("Delete Books");
        JButton buttonSearchBooks = new JButton("Search Books");
        JButton buttonExit = new JButton("Exit");

        add(buttonAddBooks);
        add(buttonIssueBooks);
        add(buttonViewIssuedBooks);
        add(buttonRegisterStudent);
        add(buttonEditBooks);
        add(buttonDeleteBooks);
        add(buttonSearchBooks);
        add(buttonExit);

        buttonAddBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddBookForm().setVisible(true);
            }
        });

        buttonIssueBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new IssueBooksForm().setVisible(true);
            }
        });

        buttonViewIssuedBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewIssuedBooksForm().setVisible(true);
            }
        });

        buttonRegisterStudent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterStudentForm().setVisible(true);
            }
        });

        buttonEditBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditBookForm().setVisible(true);
            }
        });

        buttonDeleteBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DeleteBookForm().setVisible(true);
            }
        });

        buttonSearchBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SearchBooksForm().setVisible(true);
            }
        });

        buttonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                        LibraryManagementMain.this,
                        "Are you sure you want to exit?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
