package Library;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private int quantity;
    private int availableQuantity;

    public Book(String title, String author, String isbn, String publisher, int quantity) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.quantity = quantity;
        this.availableQuantity = quantity; // All available by default
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
