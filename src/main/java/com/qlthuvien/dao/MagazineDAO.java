package com.qlthuvien.dao;

import com.qlthuvien.model.Magazine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MagazineDAO extends DocumentDAO<Magazine> {

    public MagazineDAO(Connection connection) {
        super(connection);
    }

    public void add(Magazine magazine) throws SQLException {
        String sql = "INSERT INTO magazines (title, author, publisher, issue_number, status, coverPath) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, magazine.getTitle());
            stmt.setString(2, magazine.getAuthor());
            stmt.setString(3, magazine.getPublisher());
            stmt.setInt(4, magazine.getIssueNumber());
            stmt.setString(5, magazine.getStatus());
            stmt.setString(6, magazine.getCoverPath()); // Save the cover path
            stmt.executeUpdate();
        }
    }

    public int countAvailable() throws SQLException {
        String query = "SELECT COUNT(*) FROM magazines WHERE status = 'available'";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }


    public List<Magazine> searchMagazinesByTitleOrAuthor(String query) throws SQLException {
        String sql = "SELECT * FROM magazines WHERE title LIKE ? OR author LIKE ?";
        List<Magazine> magazines = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    magazines.add(new Magazine(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("status"),
                            rs.getInt("issue_number"),
                            rs.getString("publisher"),
                            rs.getString("coverPath")
                    ));
                }
            }
        }
        return magazines;
    }

    public void update(Magazine magazine) throws SQLException {
        String sql = "UPDATE magazines SET title = ?, author = ?, publisher = ?, issue_number = ?, status = ?, coverPath = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, magazine.getTitle());
            stmt.setString(2, magazine.getAuthor());
            stmt.setString(3, magazine.getPublisher());
            stmt.setInt(4, magazine.getIssueNumber());
            stmt.setString(5, magazine.getStatus());
            stmt.setString(6, magazine.getCoverPath()); // Update the cover path
            stmt.setInt(7, magazine.getId());
            stmt.executeUpdate();
        }
    }


    @Override
    public void delete(int magazineId) throws SQLException {
        String deleteMagazineQuery = "DELETE FROM magazines WHERE id = ?";

        try (PreparedStatement magazineStmt = connection.prepareStatement(deleteMagazineQuery)) {
            magazineStmt.setInt(1, magazineId);
            magazineStmt.executeUpdate();
        }
    }

    public List<Magazine> getAll() throws SQLException {
        String sql = "SELECT * FROM magazines";
        List<Magazine> magazines = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                magazines.add(new Magazine(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("status"),
                        rs.getInt("issue_number"),
                        rs.getString("publisher"),
                        rs.getString("coverPath")
                ));
            }
        }
        return magazines;
    }


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
                            rs.getString("publisher"),
                            rs.getString("coverPath")
                    );
                }
            }
        }
        return null;
    }
}
