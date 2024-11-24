package com.qlthuvien.dao;

import com.qlthuvien.model.Thesis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThesisDAO extends DocumentDAO<Thesis> {

    public ThesisDAO(Connection connection) {
        super(connection);
    }

    public void add(Thesis thesis) throws SQLException {
        String sql = "INSERT INTO theses (title, author, supervisor, university, status, coverPath) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, thesis.getTitle());
            stmt.setString(2, thesis.getAuthor());
            stmt.setString(3, thesis.getSupervisor());
            stmt.setString(4, thesis.getUniversity());
            stmt.setString(5, thesis.getStatus());
            stmt.setString(6, thesis.getCoverPath());
            stmt.executeUpdate();
        }
    }

    public List<Thesis> searchThesesByTitleOrAuthor(String query) throws SQLException {
        String sql = "SELECT * FROM theses WHERE title LIKE ? OR author LIKE ?";
        List<Thesis> theses = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    theses.add(new Thesis(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("supervisor"),
                            rs.getString("university"),
                            rs.getString("status"),
                            rs.getString("coverPath")
                    ));
                }
            }
        }
        return theses;
    }

    public void update(Thesis thesis) throws SQLException {
        String sql = "UPDATE theses SET title = ?, author = ?, supervisor = ?, university = ?, status = ?, coverPath = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, thesis.getTitle());
            stmt.setString(2, thesis.getAuthor());
            stmt.setString(3, thesis.getSupervisor());
            stmt.setString(4, thesis.getUniversity());
            stmt.setString(5, thesis.getStatus());
            stmt.setString(6, thesis.getCoverPath());
            stmt.setInt(7, thesis.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int thesisId) throws SQLException {
        String deleteThesisQuery = "DELETE FROM theses WHERE id = ?";

        try (PreparedStatement thesisStmt = connection.prepareStatement(deleteThesisQuery)) {
            thesisStmt.setInt(1, thesisId);
            thesisStmt.executeUpdate();
        }
    }

    public List<Thesis> getAll() throws SQLException {
        String sql = "SELECT * FROM theses";
        List<Thesis> theses = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                theses.add(new Thesis(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("supervisor"),
                        rs.getString("university"),
                        rs.getString("status"),
                        rs.getString("coverPath")
                ));
            }
        }
        return theses;
    }

    @Override
    public Thesis getById(int thesisId) throws SQLException {
        String query = "SELECT id, title, author, supervisor, university, status FROM theses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, thesisId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Thesis(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("supervisor"),
                            rs.getString("university"),
                            rs.getString("status"),
                            rs.getString("coverPath")
                    );
                }
            }
        }
        return null;
    }
}
