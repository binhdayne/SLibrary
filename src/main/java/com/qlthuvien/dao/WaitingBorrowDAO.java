package com.qlthuvien.dao;

import com.qlthuvien.model.BorrowReturn;
import com.qlthuvien.model.WaitingBorrow;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaitingBorrowDAO {

    private final Connection connection;

    public WaitingBorrowDAO(Connection connection) {
        this.connection = connection;
    }

    // Hàm lấy danh sách waiting borrow
    public List<BorrowReturn> getWaitingBorrows() throws SQLException {
        List<BorrowReturn> waitingBorrows = new ArrayList<>();
        String query = "SELECT * FROM waiting_borrow";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                waitingBorrows.add(new BorrowReturn(
                        rs.getString("membership_id"),
                        rs.getInt("document_id"),
                        rs.getString("document_type"),
                        rs.getTimestamp("borrow_date") != null ? rs.getTimestamp("borrow_date").toLocalDateTime().toLocalDate() : null,
                        null,
                        rs.getString("status")
                ));
            }
        }
        return waitingBorrows;
    }


    public void addToWaitingBorrow(BorrowReturn borrowReturn) throws SQLException {
        String query = "INSERT INTO waiting_borrow (membership_id, document_id, document_type, borrow_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, borrowReturn.getMembershipId());
            stmt.setInt(2, borrowReturn.getDocumentId());
            stmt.setString(3, borrowReturn.getDocumentType());
            stmt.setTimestamp(4, Timestamp.valueOf(borrowReturn.getBorrowDate().atStartOfDay()));
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

    public void removeExpiredWaitingRecords() throws SQLException {
        String query = "DELETE FROM waiting_borrow WHERE DATEDIFF(NOW(), borrow_date) > 2";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int rowsDeleted = stmt.executeUpdate();
            System.out.println("Expired waiting records removed: " + rowsDeleted);
        }
    }

    public boolean hasExpiredWaiting(String membershipId) throws SQLException {
        String query = "SELECT COUNT(*) AS expired_count FROM waiting_borrow " +
                "WHERE membership_id = ? AND DATEDIFF(NOW(), borrow_date) > 2";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("expired_count") > 0;
            }
        }
        return false;
    }

    public void deleteFromWaitingBorrow(int documentId, String documentType) throws SQLException {
        // Câu lệnh xóa từ bảng waiting_borrow
        String deleteQuery = "DELETE FROM waiting_borrow WHERE document_id = ?";

        // Câu lệnh cập nhật trạng thái trong các bảng tài liệu tương ứng
        String updateQuery = null;
        switch (documentType.toLowerCase()) {
            case "book":
                updateQuery = "UPDATE books SET status = 'available' WHERE id = ?";
                break;
            case "thesis":
                updateQuery = "UPDATE theses SET status = 'available' WHERE id = ?";
                break;
            case "magazine":
                updateQuery = "UPDATE magazines SET status = 'available' WHERE id = ?";
                break;
            case "book_from_api":
                updateQuery = "UPDATE books_from_api SET status = 'available' WHERE id = ?";
                break;
            default:
                throw new IllegalArgumentException("Unsupported document type: " + documentType);
        }

        // Thực hiện xóa và cập nhật trạng thái
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            // Set giá trị cho document_id
            deleteStmt.setInt(1, documentId);
            updateStmt.setInt(1, documentId);

            // Thực thi xóa trước
            int rowsDeleted = deleteStmt.executeUpdate();
            if (rowsDeleted > 0) {
                // Nếu xóa thành công, cập nhật trạng thái
                updateStmt.executeUpdate();
            } else {
                throw new SQLException("No record found to delete with document_id: " + documentId);
            }
        }
    }


    public List<BorrowReturn> getWaitingTransactionsByDocumentType(String documentType) throws SQLException {
        List<BorrowReturn> waitingTransactions = new ArrayList<>();
        String query = "SELECT * FROM waiting_borrow WHERE document_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, documentType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BorrowReturn transaction = new BorrowReturn(
                        rs.getString("membership_id"),
                        rs.getInt("document_id"),
                        rs.getString("document_type"),
                        rs.getDate("borrow_date") != null ? rs.getDate("borrow_date").toLocalDate() : null,
                        null, // Không có `return_date` trong bảng waiting_borrow
                        rs.getString("status")
                );
                waitingTransactions.add(transaction);
            }
        }
        return waitingTransactions;
    }


    public List<BorrowReturn> getByStatus(String status) throws SQLException {
        List<BorrowReturn> waitingList = new ArrayList<>();
        String query = "SELECT * FROM waiting_borrow WHERE status = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                waitingList.add(new BorrowReturn(
                        resultSet.getString("membership_id"),
                        resultSet.getInt("document_id"),
                        resultSet.getString("document_type"),
                        null,
                        null,
                        resultSet.getString("status")
                ));
            }
        }
        return waitingList;
    }

    public List<WaitingBorrow> getWaitingTransactionsByUser(String membershipId) throws SQLException {
        List<WaitingBorrow> waitingTransactions = new ArrayList<>();
        String query = "SELECT membership_id, document_id, document_type, borrow_date, status FROM waiting_borrow WHERE membership_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                WaitingBorrow transaction = new WaitingBorrow(
                        rs.getString("membership_id"),
                        rs.getInt("document_id"),
                        rs.getString("document_type"),
                        rs.getString("status")
                );
                waitingTransactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error executing getTransactionsByUser for waiting_borrow: " + e.getMessage());
            throw e;
        }
        return waitingTransactions;
    }


    public List<WaitingBorrow> getWaitingBooks() throws SQLException {
        String query = "SELECT * FROM waiting_borrow WHERE status = 'Waiting'";
        List<WaitingBorrow> waitingBooks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                waitingBooks.add(new WaitingBorrow(
                        rs.getString("membership_id"),
                        rs.getInt("document_id"),
                        rs.getString("document_type"),
                        rs.getDate("borrow_date").toLocalDate(),
                        rs.getString("status")
                ));
            }
        }
        return waitingBooks;
    }


}
