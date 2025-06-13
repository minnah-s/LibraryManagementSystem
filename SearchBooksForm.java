package Library;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class SearchBooksForm extends JFrame {

    private JTextField textFieldSearch;
    private JTable tableBooks;
    private DefaultTableModel tableModel;
    private JButton buttonSearch;
    private JButton buttonClear;

    public SearchBooksForm() {
        setTitle("Search Books");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        textFieldSearch = new JTextField(20);
        buttonSearch = new JButton("Search");
        buttonClear = new JButton("Clear");
        
        searchPanel.add(new JLabel("Search by Title:"));
        searchPanel.add(textFieldSearch);
        searchPanel.add(buttonSearch);
        searchPanel.add(buttonClear);
        add(searchPanel, BorderLayout.NORTH);

        // Create table for results
        tableModel = new DefaultTableModel(new String[]{"Title", "Author", "ISBN", "Available Copies", "Total Copies"}, 0);
        tableBooks = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tableBooks);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        buttonSearch.addActionListener(e -> searchBooks());
        buttonClear.addActionListener(e -> {
            textFieldSearch.setText("");
            tableModel.setRowCount(0);
        });

        // Add enter key listener to search field
        textFieldSearch.addActionListener(e -> searchBooks());

        // Load all books initially
        searchBooks();
    }

    private void searchBooks() {
        String query = textFieldSearch.getText();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search query");
            return;
        }

        List<Book> books = BookDAO.searchBooks(query);
        DefaultTableModel model = (DefaultTableModel) tableBooks.getModel();
        model.setRowCount(0);

        for (Book book : books) {
            model.addRow(new Object[]{
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getPublisher(),
                book.getQuantity()
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No books found matching your search criteria.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SearchBooksForm form = new SearchBooksForm();
            form.setVisible(true);
        });
    }
}