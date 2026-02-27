import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibraryBook {
    // Encapsulated private attributes
    private final String isbn;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private boolean isCheckedOut;
    private String checkedOutBy;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public LibraryBook(String isbn, String title, String author, LocalDate publicationDate) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.isCheckedOut = false;
        this.checkedOutBy = null;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public LocalDate getPublicationDate() { return publicationDate; }
    public boolean isCheckedOut() { return isCheckedOut; }
    public String getCheckedOutBy() { return checkedOutBy; }

    // Setters with validation
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty())
            throw new IllegalArgumentException("Title cannot be blank.");
        this.title = title.trim();
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty())
            throw new IllegalArgumentException("Author cannot be blank.");
        this.author = author.trim();
    }

    // Controlled state transitions
    public void checkOut(String borrowerName) {
        if (isCheckedOut)
            throw new IllegalStateException("Book is already checked out by " + checkedOutBy);
        isCheckedOut = true;
        checkedOutBy = borrowerName;
        System.out.printf("Book \"%s\" checked out by %s.%n", title, borrowerName);
    }

    public void returnBook() {
        if (!isCheckedOut)
            throw new IllegalStateException("Book is not currently checked out.");
        System.out.printf("Book \"%s\" returned by %s.%n", title, checkedOutBy);
        isCheckedOut = false;
        checkedOutBy = null;
    }

    public void displayInfo() {
        System.out.println("┌─────────────────────────────────────┐");
        System.out.printf("│ ISBN     : %-26s│%n", isbn);
        System.out.printf("│ Title    : %-26s│%n", title);
        System.out.printf("│ Author   : %-26s│%n", author);
        System.out.printf("│ Published: %-26s│%n", publicationDate.format(DATE_FMT));
        System.out.printf("│ Status   : %-26s│%n",
                isCheckedOut ? "Out — " + checkedOutBy : "Available");
        System.out.println("└─────────────────────────────────────┘");
    }

    public static void main(String[] args) {
        LibraryBook book = new LibraryBook(
                "978-0-13-468599-1",
                "Effective Java",
                "Joshua Bloch",
                LocalDate.of(2018, 1, 6));

        book.displayInfo();

        book.checkOut("Alice");
        book.displayInfo();

        // Attempt double checkout
        try {
            book.checkOut("Bob");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        book.returnBook();
        book.displayInfo();
    }
}
