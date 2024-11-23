package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.qlthuvien.model.Thesis;

public class ThesisDAO extends DocumentDAO<Thesis> {
    
    /**
     * Constructor for ThesisDAO
     * @param connection
     */
    public ThesisDAO(Connection connection) {
        super(connection);
    }
    
    /**
     * Add a thesis to the database
     * @param thesis
     * @throws SQLException
     */
    @Override
    public void add(Thesis thesis) throws SQLException {
        String insertThesisQuery = "INSERT INTO theses (title, author, supervisor, university, status) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement thesisStmt = connection.prepareStatement(insertThesisQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Add something here on theses table
            thesisStmt.setString(1, thesis.getTitle());
            thesisStmt.setString(2, thesis.getAuthor());
            thesisStmt.setString(3, thesis.getSupervisor());
            thesisStmt.setString(4, thesis.getUniversity());
            thesisStmt.setString(5, thesis.getStatus());
            thesisStmt.executeUpdate();
        }
    }
    
    /**
     * Update a thesis in the database
     * @param thesis
     * @throws SQLException
     */
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

    /**
     * Delete a thesis from the database
     * @param thesisId
     * @throws SQLException
     */
    @Override
    public void delete(int thesisId) throws SQLException {
        String deleteThesisQuery = "DELETE FROM theses WHERE id = ?";

        try (PreparedStatement thesisStmt = connection.prepareStatement(deleteThesisQuery)) {
            thesisStmt.setInt(1, thesisId);
            thesisStmt.executeUpdate();
        }
    }

    /**
     * Get all theses from the database
     * @return List of theses
     * @throws SQLException
     */
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

    /**
     * Get a thesis by its ID
     * @param thesisId
     * @return Thesis
     * @throws SQLException
     */
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
