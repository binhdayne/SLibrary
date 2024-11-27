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
        String sql = "INSERT INTO users (membership_id, name, email, phone, user_name, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getMembershipId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getUser_name());
            stmt.setString(6, user.getPassword());
            stmt.executeUpdate();
        }
    }


    // Phương thức cập nhật user
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, user_name = ?, password = ? WHERE membership_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getUser_name());
            stmt.setString(5, user.getPassword());
            stmt.setString(6, user.getMembershipId());
            stmt.executeUpdate();
        }
    }


















































































































































    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ? WHERE membership_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
//            stmt.setString(4, user.getUser_name());
//            stmt.setString(5, user.getPassword());
//            stmt.setString(6, user.getMembershipId());
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
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("user_name")
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
                        rs.getString("phone"),
                        rs.getString("avatar")
                );
            }
        }
        return null;
    }

    public void updateAvatar(String userId, String avatarPath) throws SQLException {
        String query = "UPDATE users SET avatar = ? WHERE membership_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, avatarPath);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }
}
