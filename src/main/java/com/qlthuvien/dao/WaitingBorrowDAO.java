package com.qlthuvien.dao;

import com.qlthuvien.model.BorrowReturn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaitingBorrowDAO {

    private final Connection connection;

    public WaitingBorrowDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Lấy danh sách tất cả các bản ghi từ bảng waiting_borrow
     * @return Danh sách các bản ghi trong bảng waiting_borrow
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu
     */
    public List<BorrowReturn> getWaitingBorrows() throws SQLException {
        String query = "SELECT * FROM waiting_borrow";
        List<BorrowReturn> waitingBorrows = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                waitingBorrows.add(new BorrowReturn(
                        rs.getString("membership_id"),
                        rs.getInt("document_id"),
                        rs.getString("document_type"),
                        rs.getTimestamp("borrow_date").toLocalDateTime().toLocalDate(),
                        null,
                        rs.getString("status")
                ));
            }
        }
        return waitingBorrows;
    }

    /**
     * Thêm bản ghi vào bảng waiting_borrow
     * @param borrowReturn Đối tượng BorrowReturn để thêm
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình thêm dữ liệu
     */
    public void addToWaitingBorrow(BorrowReturn borrowReturn) throws SQLException {
        String query = "INSERT INTO waiting_borrow (membership_id, document_id, document_type, borrow_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, borrowReturn.getMembershipId());
            stmt.setInt(2, borrowReturn.getDocumentId());
            stmt.setString(3, borrowReturn.getDocumentType());
            stmt.setDate(4, Date.valueOf(borrowReturn.getBorrowDate())); // Chuyển LocalDate sang java.sql.Date
            stmt.setString(5, borrowReturn.getStatus());
            stmt.executeUpdate();
        }
    }

    /**
     * Đếm số lượng tài liệu đang mượn và đang chờ của một thành viên
     * @param membershipId Mã thành viên
     * @return Tổng số tài liệu đang mượn và đang chờ
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình truy vấn
     */
    public int countBorrowedAndWaiting(String membershipId) throws SQLException {
        String query = "SELECT " +
                "  (SELECT COUNT(*) FROM borrow_return WHERE membership_id = ? AND status = 'Borrowed') + " +
                "  (SELECT COUNT(*) FROM waiting_borrow WHERE membership_id = ?) AS total_count";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            stmt.setString(2, membershipId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_count");
            }
        }
        return 0; // Trả về 0 nếu không tìm thấy dữ liệu
    }

    public boolean isDocumentWaiting(String membershipId, int documentId, String documentType) throws SQLException {
        String query = "SELECT COUNT(*) FROM waiting_borrow " +
                "WHERE membership_id = ? AND document_id = ? AND document_type = ? AND status = 'Waiting'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            stmt.setInt(2, documentId);
            stmt.setString(3, documentType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public void updateDocumentStatus(int documentId, String status) throws SQLException {
        String query = "UPDATE waiting_borrow SET status = ? WHERE document_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, documentId);
            stmt.executeUpdate();
        }
    }
}
