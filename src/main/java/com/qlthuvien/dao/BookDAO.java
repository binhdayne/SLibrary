package com.qlthuvien.dao;

import com.qlthuvien.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO extends DocumentDAO<Book> {

    public BookDAO(Connection connection) {
        super(connection);
    }

    public void add(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, genre, status, coverPath) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setString(4, book.getStatus());
            stmt.setString(5, book.getBookcover()); // Save the book cover path
            stmt.executeUpdate();
        }
    }

    public List<Book> searchBooksByTitleOrAuthor(String query) throws SQLException {
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        List<Book> books = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("genre"),
                            rs.getString("status"),
                            rs.getString("coverPath")
                    ));
                }
            }
        }
        return books;
    }

    public void update(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, genre = ?, status = ?, coverPath = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getGenre());
            stmt.setString(4, book.getStatus());
            stmt.setString(5, book.getBookcover()); // Update the book cover path
            stmt.setInt(6, book.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int bookId) throws SQLException {
        String deleteBookQuery = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement bookStmt = connection.prepareStatement(deleteBookQuery)) {
            bookStmt.setInt(1, bookId);
            bookStmt.executeUpdate();
        }
    }

    public List<Book> getAll() throws SQLException {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("status"),
                        rs.getString("genre"),
                        rs.getString("coverPath") // Get the book cover path
                ));
            }
        }
        return books;
    }

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
                            rs.getString("genre"),
                            rs.getString("coverPath")
                    );
                }
            }
        }
        return null;
    }
}
