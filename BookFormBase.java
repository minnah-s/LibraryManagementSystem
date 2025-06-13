package Library;

import javax.swing.*;

public abstract class BookFormBase extends JFrame {

    protected JComboBox<String> comboBoxTitles;
    protected JTextField textFieldTitle;   // Initialize this field
    protected JTextField textFieldAuthor;
    protected JTextField textFieldISBN;
    protected JTextField textFieldQuantity;

    protected JButton buttonSave;
    protected JButton buttonDelete;
    protected JButton buttonCancel;

    public BookFormBase(String windowTitle) {
        setTitle(windowTitle);
        setSize(400, 400);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        addListeners();
        clearFields(); // Clear fields after components are initialized
    }

    private void initComponents() {
        JLabel labelSelectTitle = new JLabel("Title:");
        labelSelectTitle.setBounds(30, 30, 100, 25);
        add(labelSelectTitle);

        comboBoxTitles = new JComboBox<>();
        comboBoxTitles.setBounds(140, 30, 200, 25);
        add(comboBoxTitles);

        // Initialize textFieldTitle
        JLabel labelTitle = new JLabel("Book Title:");
        labelTitle.setBounds(30, 70, 100, 25);
        add(labelTitle);

        textFieldTitle = new JTextField(); // Initialize textFieldTitle
        textFieldTitle.setBounds(140, 70, 200, 25);
        add(textFieldTitle);

        JLabel labelAuthor = new JLabel("Author:");
        labelAuthor.setBounds(30, 110, 100, 25);
        add(labelAuthor);

        textFieldAuthor = new JTextField();
        textFieldAuthor.setBounds(140, 110, 200, 25);
        add(textFieldAuthor);

        JLabel labelISBN = new JLabel("ISBN:");
        labelISBN.setBounds(30, 150, 100, 25);
        add(labelISBN);

        textFieldISBN = new JTextField();
        textFieldISBN.setBounds(140, 150, 200, 25);
        add(textFieldISBN);

        JLabel labelQuantity = new JLabel("Quantity:");
        labelQuantity.setBounds(30, 190, 100, 25);
        add(labelQuantity);

        textFieldQuantity = new JTextField();
        textFieldQuantity.setBounds(140, 190, 200, 25);
        add(textFieldQuantity);

        buttonSave = new JButton("Save");
        buttonSave.setBounds(140, 230, 90, 30);
        add(buttonSave);

        buttonDelete = new JButton("Delete");
        buttonDelete.setBounds(250, 230, 90, 30);
        add(buttonDelete);

        buttonCancel = new JButton("Cancel");
        buttonCancel.setBounds(140, 270, 90, 30);
        add(buttonCancel);
    }

    private void addListeners() {
        buttonSave.addActionListener(e -> saveBook());
        buttonDelete.addActionListener(e -> deleteBook());
        buttonCancel.addActionListener(e -> dispose());
        comboBoxTitles.addActionListener(e -> loadBook()); // Automatically load book details when selected
    }

    // Abstract methods to be implemented in subclass
    protected abstract void loadBook();

    protected abstract void saveBook();

    protected abstract void deleteBook();

    protected void clearFields() {
        textFieldTitle.setText("");   // Now this will not throw NullPointerException
        textFieldAuthor.setText("");
        textFieldISBN.setText("");
        textFieldQuantity.setText("");
    }
}