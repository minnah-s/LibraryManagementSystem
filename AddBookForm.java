package Library;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddBookForm extends JFrame {

    private JLabel labelTitle, labelAuthor, labelISBN, labelPublisher, labelQuantity;
    private JTextField textFieldTitle, textFieldAuthor, textFieldISBN, textFieldPublisher, textFieldQuantity;
    private JButton buttonAdd, buttonCancel;

    public AddBookForm() {
        setTitle("Add Book");
        setSize(320, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Initialize labels and fields
        labelTitle = new JLabel("Title:");
        labelAuthor = new JLabel("Author:");
        labelISBN = new JLabel("ISBN:");
        labelPublisher = new JLabel("Publisher:");
        labelQuantity = new JLabel("Quantity:");

        textFieldTitle = new JTextField();
        textFieldAuthor = new JTextField();
        textFieldISBN = new JTextField();
        textFieldPublisher = new JTextField();
        textFieldQuantity = new JTextField();

        buttonAdd = new JButton("Add Book");
        buttonCancel = new JButton("Cancel");

        // Set bounds
        labelTitle.setBounds(30, 30, 80, 25);
        textFieldTitle.setBounds(120, 30, 150, 25);

        labelAuthor.setBounds(30, 70, 80, 25);
        textFieldAuthor.setBounds(120, 70, 150, 25);

        labelISBN.setBounds(30, 110, 80, 25);
        textFieldISBN.setBounds(120, 110, 150, 25);

        labelPublisher.setBounds(30, 150, 80, 25);
        textFieldPublisher.setBounds(120, 150, 150, 25);

        labelQuantity.setBounds(30, 190, 80, 25);
        textFieldQuantity.setBounds(120, 190, 150, 25);

        buttonAdd.setBounds(30, 240, 100, 30);
        buttonCancel.setBounds(150, 240, 100, 30);

        // Add to form
        add(labelTitle);
        add(textFieldTitle);
        add(labelAuthor);
        add(textFieldAuthor);
        add(labelISBN);
        add(textFieldISBN);
        add(labelPublisher);
        add(textFieldPublisher);
        add(labelQuantity);
        add(textFieldQuantity);
        add(buttonAdd);
        add(buttonCancel);

        buttonAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddBook();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void handleAddBook() {
        try {
            String title = textFieldTitle.getText().trim();
            String author = textFieldAuthor.getText().trim();
            String isbn = textFieldISBN.getText().trim();
            String publisher = textFieldPublisher.getText().trim();
            int quantity = Integer.parseInt(textFieldQuantity.getText().trim());

            Book book = new Book(title, author, isbn, publisher, quantity);
            boolean success = BookDAO.addBook(book);

            if (success) {
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields.");
        }
    }

    private void clearFields() {
        textFieldTitle.setText("");
        textFieldAuthor.setText("");
        textFieldISBN.setText("");
        textFieldPublisher.setText("");
        textFieldQuantity.setText("");
    }
}
