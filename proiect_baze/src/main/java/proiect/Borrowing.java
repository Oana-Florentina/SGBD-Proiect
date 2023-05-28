package proiect;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class Borrowing {
    private int id;
    private User user;
    private Book book;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Borrowing(int id, User user, Book book, LocalDate dueDate) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.dueDate = dueDate;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Borrowing{" +
                "id=" + id +
                ", user=" + user.getName() +
                ", book=" + book.getTitle() +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
