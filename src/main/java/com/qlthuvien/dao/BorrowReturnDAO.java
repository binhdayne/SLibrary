package com.qlthuvien.dao;

import com.qlthuvien.model.BorrowReturn;
import com.qlthuvien.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BorrowReturnDAO {

    private final Connection connection;

    public BorrowReturnDAO(Connection connection) {
        this.connection = connection;
    }

    // Thêm mới giao dịch mượn tài liệu và cập nhật trạng thái của tài liệu
    public void borrowDocument(BorrowReturn borrowReturn) throws SQLException {
        String insertQuery = "INSERT INTO borrow_return (membership_id, document_id, document_type, borrow_date, return_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        String updateStatusQuery = "UPDATE %s SET status = 'borrowed' WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            // Thêm giao dịch mượn vào bảng borrow_return
            try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
                stmt.setString(1, borrowReturn.getMembershipId());
                stmt.setInt(2, borrowReturn.getDocumentId());
                stmt.setString(3, borrowReturn.getDocumentType());
                stmt.setDate(4, Date.valueOf(borrowReturn.getBorrowDate()));
                stmt.setNull(5, Types.DATE); // Khi mượn tài liệu, chưa có return_date
                stmt.setString(6, borrowReturn.getStatus());
                stmt.executeUpdate();
            }

            // Cập nhật trạng thái của tài liệu sang "borrowed" trong bảng tương ứng
            String documentTable = getDocumentTable(borrowReturn.getDocumentType());
            String updateQuery = String.format(updateStatusQuery, documentTable);
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, borrowReturn.getDocumentId());
                updateStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Cập nhật giao dịch trả tài liệu và cập nhật trạng thái của tài liệu
    public void returnDocument(BorrowReturn borrowReturn) throws SQLException {
        String updateBorrowReturnQuery = "UPDATE borrow_return SET return_date = ?, status = ? WHERE membership_id = ? AND document_id = ? AND document_type = ?";
        String updateStatusQuery = "UPDATE %s SET status = 'available' WHERE id = ?";

        try {
            connection.setAutoCommit(false);

            // Cập nhật thông tin trả tài liệu trong bảng borrow_return
            try (PreparedStatement stmt = connection.prepareStatement(updateBorrowReturnQuery)) {
                stmt.setDate(1, Date.valueOf(borrowReturn.getReturnDate()));
                stmt.setString(2, borrowReturn.getStatus());
                stmt.setString(3, borrowReturn.getMembershipId());
                stmt.setInt(4, borrowReturn.getDocumentId());
                stmt.setString(5, borrowReturn.getDocumentType());
                stmt.executeUpdate();
            }

            // Cập nhật trạng thái của tài liệu sang "available" trong bảng tương ứng
            String documentTable = getDocumentTable(borrowReturn.getDocumentType());
            String updateQuery = String.format(updateStatusQuery, documentTable);
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, borrowReturn.getDocumentId());
                updateStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Lấy danh sách tất cả các giao dịch mượn trả tài liệu
    public List<BorrowReturn> getAll() throws SQLException {
        List<BorrowReturn> transactions = new ArrayList<>();
        String query = "SELECT * FROM borrow_return";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                BorrowReturn transaction = new BorrowReturn(
                        rs.getString("membership_id"),
                        rs.getInt("document_id"),
                        rs.getString("document_type"),
                        rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getString("status")
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public List<BorrowReturn> getTransactionsByUser(String membershipId) throws SQLException {
        // Danh sách để lưu trữ các giao dịch BorrowReturn
        List<BorrowReturn> transactions = new ArrayList<>();

        // Câu lệnh SQL để truy vấn dữ liệu
        String query = "SELECT membership_id, document_id, document_type, borrow_date, return_date, status FROM borrow_return WHERE membership_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Thiết lập giá trị tham số cho câu lệnh SQL
            stmt.setString(1, membershipId);

            // Thực thi câu lệnh SQL
            ResultSet rs = stmt.executeQuery();

            // Lặp qua kết quả truy vấn
            while (rs.next()) {
                // Lấy giá trị từ các cột trong cơ sở dữ liệu
                String membershipIdResult = rs.getString("membership_id");
                int documentId = rs.getInt("document_id");
                String documentType = rs.getString("document_type");
                LocalDate borrowDate = rs.getDate("borrow_date").toLocalDate();

                // Kiểm tra và xử lý giá trị null trong cột return_date
                LocalDate returnDate = rs.getDate("return_date") != null
                        ? rs.getDate("return_date").toLocalDate()
                        : null;

                String status = rs.getString("status");

                // Tạo đối tượng BorrowReturn từ dữ liệu và thêm vào danh sách
                BorrowReturn transaction = new BorrowReturn(
                        membershipIdResult,
                        documentId,
                        documentType,
                        borrowDate,
                        returnDate,
                        status
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            // Log lỗi hoặc xử lý ngoại lệ nếu cần
            System.err.println("Error fetching transactions: " + e.getMessage());
            throw e;
        }

        return transactions; // Trả về danh sách giao dịch
    }

    public List<BorrowReturn> getTransactionsByDocumentType(String documentType) throws SQLException {
        List<BorrowReturn> transactions = new ArrayList<>();
        String query = "SELECT * FROM borrow_return WHERE document_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, documentType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BorrowReturn transaction = new BorrowReturn(
                        rs.getString("membership_id"),
                        rs.getInt("document_id"),
                        rs.getString("document_type"),
                        rs.getDate("borrow_date").toLocalDate(),
                        rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                        rs.getString("status")
                );
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public String getDocumentDetails(String documentType, int documentId) throws SQLException {
        String documentTable = getDocumentTable(documentType);
        String query = String.format("SELECT * FROM %s WHERE id = ?", documentTable);

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, documentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    StringBuilder details = new StringBuilder("Title: ").append(rs.getString("title"))
                            .append(", Author: ").append(rs.getString("author"));

                    switch (documentType.toUpperCase()) {
                        case "BOOK":
                            details.append(", Genre: ").append(rs.getString("genre"))
                                    .append(", Status: ").append(rs.getString("status"));
                            break;
                        case "MAGAZINE":
                            details.append(", Issue Number: ").append(rs.getInt("issue_number"))
                                    .append(", Publisher: ").append(rs.getString("publisher"))
                                    .append(", Status: ").append(rs.getString("status"));
                            break;
                        case "THESIS":
                            details.append(", Supervisor: ").append(rs.getString("supervisor"))
                                    .append(", University: ").append(rs.getString("university"))
                                    .append(", Status: ").append(rs.getString("status"));
                            break;
                        case "BOOK_FROM_API":
                            details.append(", ISBN: ").append(rs.getString("isbn"))
                                    .append(", Publisher: ").append(rs.getString("publisher"))
                                    .append(", Published Date: ").append(rs.getString("published_date"))
                                    .append(", Status: ").append(rs.getString("status"));
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown document type: " + documentType);
                    }
                    return details.toString();
                } else {
                    return "Document not found.";
                }
            }
        }
    }

    public String getDocumentStatus(String documentType, int documentId) throws SQLException {
        String documentTable = getDocumentTable(documentType);
        String query = String.format("SELECT status FROM %s WHERE id = ?", documentTable);

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, documentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                } else {
                    throw new SQLException("Document not found.");
                }
            }
        }
    }

    // Lấy thông tin mượn của tài liệu
    public BorrowReturn getBorrowInfo(String documentType, int documentId) throws SQLException {
        String query = "SELECT * FROM borrow_return WHERE document_type = ? AND document_id = ? AND status = 'Borrowed' or 'Waiting'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, documentType);
            stmt.setInt(2, documentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new BorrowReturn(
                            rs.getString("membership_id"),
                            rs.getInt("document_id"),
                            rs.getString("document_type"),
                            rs.getDate("borrow_date").toLocalDate(),
                            rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                            rs.getString("status")
                    );
                } else {
                    throw new SQLException("No borrow information found for this document.");
                }
            }
        }
    }

    // Phương thức hỗ trợ để xác định bảng dựa trên loại tài liệu
    public String getDocumentTable(String documentType) {
        switch (documentType) {
            case "BOOK":
                return "books";
            case "MAGAZINE":
                return "magazines";
            case "THESIS":
                return "theses";
            case "BOOK_FROM_API":
                return "books_from_api"; // Thêm ánh xạ cho loại tài liệu này
            default:
                throw new IllegalArgumentException("Unknown document type: " + documentType);
        }
    }

    public List<BorrowReturn> getByStatus(String status) throws SQLException {
        String query = "SELECT * FROM borrow_return WHERE status = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                List<BorrowReturn> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(new BorrowReturn(
                            rs.getString("membership_id"),
                            rs.getInt("document_id"),
                            rs.getString("document_type"),
                            rs.getDate("borrow_date").toLocalDate(),
                            rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
                            rs.getString("status")
                    ));
                }
                return results;
            }
        }
    }

    public static List<Map<String, String>> getWaitingBorrowedItems() {
        List<Map<String, String>> items = new ArrayList<>();
        String sql = "SELECT membership_id, document_id, document_type, borrow_date, status FROM waiting_borrow WHERE status = 'Waiting'";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(Map.of(
                        "membership_id", rs.getString("membership_id"),
                        "document_id", rs.getString("document_id"),
                        "document_type", rs.getString("document_type"),
                        "borrow_date", rs.getString("borrow_date"),
                        "status", rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

}
