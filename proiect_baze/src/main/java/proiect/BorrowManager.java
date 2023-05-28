package proiect;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "student";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            User user = new User("Flore", "Floryy@gmail.com", "A_passwordx2");

            addUser(conn, user);
            System.out.println("User added successfully!");

            Book book = new Book("The picture of Dorian Gray", "Oscar Wilde", "Classic");
            LocalDate dueDate = LocalDate.now().plusDays(14); // Due date is set to 14 days from today
            Borrowing borrowing = borrowBook(conn, user, book, dueDate);
            System.out.println("Book borrowed successfully!");
            System.out.println("Borrowing details: " + borrowing);

            returnBook(conn, user.getId(), book.getBookId(conn,book),borrowing.getId() );
            // Return the book
            //returnBook(conn, 6, 25, 7);
            System.out.println("Book returned successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addUser(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                user.setId(userId); // Set the generated user ID in the User object
            }
        }
    }


    private static Borrowing borrowBook(Connection conn, User user, Book book, LocalDate dueDate) throws SQLException {
        // Check if the book is available for borrowing
        if (isBookAvailable(conn, book)) {
            // Insert a new borrowing record
            String sql = "INSERT INTO borrowings (user_id, book_id, due_date) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, user.getId());
                stmt.setInt(2, book.getBookId(conn, book)); // Retrieve the book ID
                stmt.setDate(3, java.sql.Date.valueOf(dueDate));
                stmt.executeUpdate();

                // Get the generated borrowing ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int borrowingId = rs.getInt(1);
                    return new Borrowing(borrowingId, user, book, dueDate);
                }
            }
        }
        return null; // Book not available for borrowing
    }



    private static void returnBook(Connection conn, int userId, int bookId, int borrowingId) throws SQLException {
        // Update the borrowing record to set the return date
        String sql = "UPDATE borrowings SET return_date = ? WHERE id = ? AND user_id = ? AND book_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setInt(2, borrowingId);
            stmt.setInt(3, userId);
            stmt.setInt(4, bookId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("Return book failed. Make sure the user and book ID are correct.");
            }
        }
    }

    private static boolean isBookAvailable(Connection conn, Book book) throws SQLException {
        // Check if the book has any active borrowings (not returned)
        String sql = "SELECT COUNT(*) FROM borrowings WHERE book_id = ? AND return_date IS NULL";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, book.getBookId(conn, book));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0; // Book is available if there are no active borrowings
            }
        }
        return false; // Error occurred while checking availability
    }
}