package proiect;

import java.sql.*;

public class BookManager {
    private static final String DB_URL =  "jdbc:postgresql://localhost:5432/db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "student";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Book book = new Book("Emma", "Jane Austen", "Classic");

            // Call the add_book procedure
            addBook(conn, book);
            //System.out.println("Book added successfully!");

            // Call the get_books_by_genre procedure
            getBooksByGenre(conn, "Roman");

            // Call the count_books_by_author function
            int bookCount = countBooksByAuthor(conn, "Jane Austen");
            System.out.println("Number of books by Jane Austen " + bookCount);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void getBooksByGenre(Connection conn, String genre) throws SQLException {
        String sql = "CALL get_books_by_genre(?, ?)";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, genre);
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.execute();

            int bookCount = stmt.getInt(2);
            System.out.println("Number of books in the '" + genre + "' genre: " + bookCount);

            String selectTempTableSql = "SELECT title, author FROM temp_books";
            try (PreparedStatement tempStmt = conn.prepareStatement(selectTempTableSql)) {
                ResultSet rs = tempStmt.executeQuery();
                while (rs.next()) {
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    System.out.println("Title: " + title + ", Author: " + author);
                }
            }
        }
    }





    private static int countBooksByAuthor(Connection conn, String author) throws SQLException {
        String sql = "CALL count_books_by_author(?, ?)";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, author);
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.execute();
            return stmt.getInt(2);
        }
    }


    private static void addBook(Connection conn, Book book) throws SQLException {
        String sql = "CALL add_book(?, ?, ?)";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.execute();
        }
    }




}