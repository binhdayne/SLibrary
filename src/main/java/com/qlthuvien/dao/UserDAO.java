package com.qlthuvien.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.qlthuvien.model.User;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }
    
    // Method add User
    public void add(User user) throws SQLException {
        String query = "INSERT INTO users (membership_id, name, email, phone) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getMembershipId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.executeUpdate();
        }
    }

    // Method update User
    public void update(User user) throws SQLException {
        String query = "UPDATE users SET name = ?, email = ?, phone = ? WHERE membership_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getMembershipId());
            stmt.executeUpdate();
        }
    }

    // Method delete User
    public void delete(String membershipId) throws SQLException {
        String query = "DELETE FROM users WHERE membership_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            stmt.executeUpdate();
        }
    }

    // Method get all User
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("membership_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                users.add(user);
            }
        }
        return users;
    }

    // Method find User by Membership ID
    public User findById(String membershipId) throws SQLException {
        String query = "SELECT * FROM users WHERE membership_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getString("membership_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
            }
        }
        return null;
    }
}
