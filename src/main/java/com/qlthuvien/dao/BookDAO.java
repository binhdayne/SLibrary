package com.qlthuvien.dao;

import com.qlthuvien.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO extends DocumentDAO<Book> {

    public BookDAO(Connection connection) {
        super(connection);
    }

    @Override
    public void add(Book book) throws SQLException {
        String insertBookQuery = "INSERT INTO books (title, author, genre, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement bookStmt = connection.prepareStatement(insertBookQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Thêm tài liệu vào bảng books
            bookStmt.setString(1, book.getTitle());
            bookStmt.setString(2, book.getAuthor());
            bookStmt.setString(3, book.getGenre());
            bookStmt.setString(4, book.getStatus());
            bookStmt.executeUpdate();
        }
    }

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

    @Override
    public void delete(int bookId) throws SQLException {
        String deleteBookQuery = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement bookStmt = connection.prepareStatement(deleteBookQuery)) {
            bookStmt.setInt(1, bookId);
            bookStmt.executeUpdate();
        }
    }

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
