package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.qlthuvien.model.BookFromAPI;

public class BookFromAPIDAO extends DocumentDAO<BookFromAPI> {
    
    /**
     * Constructor for BookFromAPIDAO
     * @param connection
     */
    public BookFromAPIDAO(Connection connection) {
        super(connection);
    }
    
    /**
     * Add a book to the database
     * @param book
     * @throws SQLException
     */
    @Override
    public void add(BookFromAPI book) throws SQLException {
        String insertBookQuery = "INSERT INTO books_from_api (isbn, title, author, publisher, published_date, description, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement bookStmt = connection.prepareStatement(insertBookQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Add something here on books_from_api table
            bookStmt.setString(1, book.getIsbn());
            bookStmt.setString(2, book.getTitle());
            bookStmt.setString(3, book.getAuthor());
            bookStmt.setString(4, book.getPublisher());
            bookStmt.setString(5, book.getPublishedDate());
            bookStmt.setString(6, book.getDescription());
            bookStmt.setString(7, book.getStatus());
            bookStmt.executeUpdate();
        }
    }
    
    /**
     * Update a book in the database
     * @param book
     * @throws SQLException
     */
    @Override
    public void update(BookFromAPI book) throws SQLException {
        String query = "UPDATE books_from_api SET title = ?, author = ?, publisher = ?, published_date = ?, description = ?, status = ? WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getPublishedDate());
            stmt.setString(5, book.getDescription());
            stmt.setString(6, book.getStatus());
            stmt.setString(7, book.getIsbn());
            stmt.executeUpdate();
        }
    }

    /**
     * Delete a book from the database
     * @param bookId
     * @throws SQLException
     */
    public void delete(int bookId) throws SQLException {
        String deleteBookQuery = "DELETE FROM books_from_api WHERE id = ?";
        try (PreparedStatement bookStmt = connection.prepareStatement(deleteBookQuery)) {
            bookStmt.setInt(1, bookId);
            bookStmt.executeUpdate();
        }
    }
    
    /**
     * Delete a book from the database by its ISBN
     * @param isbn
     * @throws SQLException
     */
    public void deleteByIsbn(String isbn) throws SQLException {
        String query = "DELETE FROM books_from_api WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        }
    }

    /**
     * Get all books from the database
     * @return List of books
     * @throws SQLException
     */
    @Override
    public List<BookFromAPI> getAll() throws SQLException {
        List<BookFromAPI> books = new ArrayList<>();
        String query = "SELECT id, isbn, title, author, publisher, published_date, description, status FROM books_from_api";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                BookFromAPI book = new BookFromAPI(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("status"),
                        rs.getString("isbn"),
                        rs.getString("publisher"),
                        rs.getString("published_date"),
                        rs.getString("description")
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
    public BookFromAPI getById(int bookId) throws SQLException {
        String query = "SELECT id, isbn, title, author, publisher, published_date, description, status FROM books_from_api WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new BookFromAPI(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("status"),
                            rs.getString("isbn"),
                            rs.getString("publisher"),
                            rs.getString("published_date"),
                            rs.getString("description")
                    );
                }
            }
        }
        return null;
    }
}
