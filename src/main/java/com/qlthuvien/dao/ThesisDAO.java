package com.qlthuvien.dao;

import com.qlthuvien.model.Thesis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThesisDAO extends DocumentDAO<Thesis> {

    public ThesisDAO(Connection connection) {
        super(connection);
    }

    @Override
    public void add(Thesis thesis) throws SQLException {
        String insertThesisQuery = "INSERT INTO theses (title, author, supervisor, university, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement thesisStmt = connection.prepareStatement(insertThesisQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Thêm vào bảng theses
            thesisStmt.setString(1, thesis.getTitle());
            thesisStmt.setString(2, thesis.getAuthor());
            thesisStmt.setString(3, thesis.getSupervisor());
            thesisStmt.setString(4, thesis.getUniversity());
            thesisStmt.setString(5, thesis.getStatus());
            thesisStmt.executeUpdate();
        }
    }

    @Override
    public void update(Thesis thesis) throws SQLException {
        String query = "UPDATE theses SET title = ?, author = ?, supervisor = ?, university = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, thesis.getTitle());
            stmt.setString(2, thesis.getAuthor());
            stmt.setString(3, thesis.getSupervisor());
            stmt.setString(4, thesis.getUniversity());
            stmt.setString(5, thesis.getStatus());
            stmt.setInt(6, thesis.getId());
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

    @Override
    public List<Thesis> getAll() throws SQLException {
        String query = "SELECT id, title, author, supervisor, university, status FROM theses";
        List<Thesis> theses = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Thesis thesis = new Thesis(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("status"),
                        rs.getString("supervisor"),
                        rs.getString("university")
                );
                theses.add(thesis);
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
                            rs.getString("status"),
                            rs.getString("supervisor"),
                            rs.getString("university")
                    );
                }
            }
        }
        return null;
    }
}
