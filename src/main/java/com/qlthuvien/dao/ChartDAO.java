package com.qlthuvien.dao;

import com.qlthuvien.utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ChartDAO {

    // Phương thức lấy số lượt mượn sách theo membership_id
    public static Map<String, Integer> getBorrowCountsByMembership() {
        Map<String, Integer> borrowCounts = new HashMap<>();

        String query = "SELECT membership_id, COUNT(*) AS borrow_count " +
                "FROM borrow_return " +
                "GROUP BY membership_id " +
                "ORDER BY borrow_count ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String membershipId = rs.getString("membership_id");
                int borrowCount = rs.getInt("borrow_count");
                borrowCounts.put(membershipId, borrowCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return borrowCounts;
    }
    public static Map<String, Integer> getRecordCountsByTable() {
        Map<String, Integer> tableCounts = new HashMap<>();

        // Mảng chứa tên các bảng cần đếm
        String[] tables = {"books", "magazines", "theses", "books_from_api"};

        try (Connection connection = DBConnection.getConnection()) {

            for (String table : tables) {
                // Tạo truy vấn đếm số dòng
                String query = "SELECT COUNT(*) AS count FROM " + table;

                try (PreparedStatement statement = connection.prepareStatement(query);
                     ResultSet resultSet = statement.executeQuery()) {

                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        tableCounts.put(table, count);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tableCounts;
    }


}
