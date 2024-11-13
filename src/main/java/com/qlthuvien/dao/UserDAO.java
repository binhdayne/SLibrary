package com.qlthuvien.dao;

import com.qlthuvien.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Phương thức thêm user
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

    // Phương thức cập nhật user
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

    // Phương thức xóa user
    public void delete(String membershipId) throws SQLException {
        String query = "DELETE FROM users WHERE membership_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            stmt.executeUpdate();
        }
    }

    // Phương thức lấy danh sách tất cả user
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

    // Tìm kiếm User theo Membership ID
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
