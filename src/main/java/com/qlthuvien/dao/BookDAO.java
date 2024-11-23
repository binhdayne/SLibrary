package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.qlthuvien.model.Book;

public class BookDAO extends DocumentDAO<Book> {
    
    /**
     * Constructor for BookDAO
     * @param connection
     */
    public BookDAO(Connection connection) {
        super(connection);
    }
    
    /**
     * Add a book to the database
     * @param book
     * @throws SQLException
     */
    @Override
    public void add(Book book) throws SQLException {
        String insertBookQuery = "INSERT INTO books (title, author, genre, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement bookStmt = connection.prepareStatement(insertBookQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Add something here
            bookStmt.setString(1, book.getTitle());
            bookStmt.setString(2, book.getAuthor());
            bookStmt.setString(3, book.getGenre());
            bookStmt.setString(4, book.getStatus());
            bookStmt.executeUpdate();
        }
    }
    
    /**
     * Update a book in the database
     * @param book
     * @throws SQLException
     */
    @Override
    public void update(Book book) throws SQLException {
        String query = "UPDATE books SET title = ?, author = ?, genre = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setString(4, book.getStatus());
            stmt.setInt(5, book.getId());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Delete a book from the database
     * @param bookId
     * @throws SQLException
     */
    @Override
    public void delete(int bookId) throws SQLException {
        String deleteBookQuery = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement bookStmt = connection.prepareStatement(deleteBookQuery)) {
            bookStmt.setInt(1, bookId);
            bookStmt.executeUpdate();
        }
    }

    /**
     * Get all books from the database
     * @return List of books
     * @throws SQLException
     */
    @Override
    public List<Book> getAll() throws SQLException {
        String query = "SELECT id, title, author, genre, status FROM books";
        List<Book> books = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("status"),
                        rs.getString("genre")
                );
                books.add(book);
            }
        }
        return books;
    }

    /**
     * Get a book by its ID
     * @param bookId
     * @return Book
     * @throws SQLException
     */
    @Override
    public Book getById(int bookId) throws SQLException {
        String query = "SELECT id, title, author, genre, status FROM books WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("status"),
                            rs.getString("genre")
                    );
                }
            }
        }
        return null;
    }
}
