package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.qlthuvien.model.Magazine;

public class MagazineDAO extends DocumentDAO<Magazine> {
    
    /**
     * Constructor for MagazineDAO
     * @param connection
     */
    public MagazineDAO(Connection connection) {
        super(connection);
    }
    
    /**
     * Add a magazine to the database
     * @param magazine
     * @throws SQLException
     */
    @Override
    public void add(Magazine magazine) throws SQLException {
        String insertMagazineQuery = "INSERT INTO magazines (title, author, issue_number, publisher, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement magazineStmt = connection.prepareStatement(insertMagazineQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Add something here on magazines table
            magazineStmt.setString(1, magazine.getTitle());
            magazineStmt.setString(2, magazine.getAuthor());
            magazineStmt.setInt(3, magazine.getIssueNumber());
            magazineStmt.setString(4, magazine.getPublisher());
            magazineStmt.setString(5, magazine.getStatus());
            magazineStmt.executeUpdate();
        }
    }
    
    /**
     * Update a magazine in the database
     * @param magazine
     * @throws SQLException
     */
    @Override
    public void update(Magazine magazine) throws SQLException {
        String query = "UPDATE magazines SET title = ?, author = ?, issue_number = ?, publisher = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, magazine.getTitle());
            stmt.setString(2, magazine.getAuthor());
            stmt.setInt(3, magazine.getIssueNumber());
            stmt.setString(4, magazine.getPublisher());
            stmt.setString(5, magazine.getStatus());
            stmt.setInt(6, magazine.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Delete a magazine from the database
     * @param magazineId
     * @throws SQLException
     */
    @Override
    public void delete(int magazineId) throws SQLException {
        String deleteMagazineQuery = "DELETE FROM magazines WHERE id = ?";

        try (PreparedStatement magazineStmt = connection.prepareStatement(deleteMagazineQuery)) {
            magazineStmt.setInt(1, magazineId);
            magazineStmt.executeUpdate();
        }
    }

    /**
     * Get all magazines from the database
     * @return List of magazines
     * @throws SQLException
     */
    @Override
    public List<Magazine> getAll() throws SQLException {
        String query = "SELECT id, title, author, issue_number, publisher, status FROM magazines";
        List<Magazine> magazines = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Magazine magazine = new Magazine(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("status"),
                        rs.getInt("issue_number"),
                        rs.getString("publisher")
                );
                magazines.add(magazine);
            }
        }
        return magazines;
    }

    /**
     * Get a magazine by its ID
     * @param magazineId
     * @return Magazine
     * @throws SQLException
     */
    @Override
    public Magazine getById(int magazineId) throws SQLException {
        String query = "SELECT id, title, author, issue_number, publisher, status FROM magazines WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, magazineId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Magazine(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("status"),
                            rs.getInt("issue_number"),
                            rs.getString("publisher")
                    );
                }
            }
        }
        return null;
    }
}
