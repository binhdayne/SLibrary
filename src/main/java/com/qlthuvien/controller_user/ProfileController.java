package com.qlthuvien.controller_user;

import com.qlthuvien.dao.BorrowReturnDAO;
import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.model.BorrowReturn;
import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ProfileController {

    @FXML
    private TableView<BorrowReturn> borrowReturnTable;

    @FXML
    private TableColumn<BorrowReturn, String> membershipIdColumn;
    @FXML
    private TableColumn<BorrowReturn, String> documentIdColumn;
    @FXML
    private TableColumn<BorrowReturn, String> documentTypeColumn;
    @FXML
    private TableColumn<BorrowReturn, LocalDate> borrowDateColumn;
    @FXML
    private TableColumn<BorrowReturn, LocalDate> returnDateColumn;
    @FXML
    private TableColumn<BorrowReturn, String> statusColumn;

    @FXML
    private TextField nameField, emailField, phoneField;
    @FXML
    private Button editButton, saveButton;

    @FXML
    private Label totalBorrowedLabel, totalReturnedLabel, pendingReturnsLabel, nearestDueDateLabel;

    @FXML
    private ImageView rankImageView;
    // Hàm cập nhật thông tin Dashboard

    private Connection connection;
    private BorrowReturnDAO borrowReturnDAO;
    private UserDAO userDAO;
    private String userId;

    public ProfileController() {
        connection = DBConnection.getConnection();
        borrowReturnDAO = new BorrowReturnDAO(connection);
        userDAO = new UserDAO(connection);
    }

    public void setUserId(String userId) {
        this.userId = userId;
        loadUserInfo(userId);
        refreshBorrowReturnTableForUser(userId);
        updateDashboard(userId); // Thêm dòng này để cập nhật Dashboard
    }

    @FXML
    public void initialize() {
        // Set column bindings
        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        documentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set column widths dynamically
        membershipIdColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        documentIdColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        documentTypeColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        borrowDateColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        returnDateColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        statusColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
    }

    private void loadUserInfo(String membershipId) {
        try {
            User user = userDAO.findById(membershipId);
            if (user != null) {
                nameField.setText(user.getName());
                emailField.setText(user.getEmail());
                phoneField.setText(user.getPhone());
            } else {
                showError("User not found.");
                clearUserInfo();
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void enableEdit() {
        // Bật chế độ chỉnh sửa
        nameField.setDisable(false);
        emailField.setDisable(false);
        phoneField.setDisable(false);
        saveButton.setDisable(false);
        editButton.setDisable(true);
    }

    @FXML
    private void saveUserInfo() {
        String newName = nameField.getText();
        String newEmail = emailField.getText();
        String newPhone = phoneField.getText();

        try {
            User updatedUser = new User(userId, newName, newEmail, newPhone);
            userDAO.update(updatedUser);  // Giả sử UserDAO có phương thức updateUser
            showSuccess("User information updated successfully!");

            // Tắt chế độ chỉnh sửa
            nameField.setDisable(true);
            emailField.setDisable(true);
            phoneField.setDisable(true);
            saveButton.setDisable(true);
            editButton.setDisable(false);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void refreshBorrowReturnTableForUser(String membershipId) {
        try {
            List<BorrowReturn> transactions = borrowReturnDAO.getTransactionsByUser(membershipId);
            borrowReturnTable.getItems().setAll(transactions);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void clearUserInfo() {
        nameField.setText("N/A");
        emailField.setText("N/A");
        phoneField.setText("N/A");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
    private void updateDashboard(String membershipId) {
        try {
            // Get user transactions
            List<BorrowReturn> transactions = borrowReturnDAO.getTransactionsByUser(membershipId);

            // Calculate statistics
            long totalBorrowed = transactions.size();
            long totalReturned = transactions.stream().filter(t -> "Returned".equalsIgnoreCase(t.getStatus())).count();
            long pendingReturns = totalBorrowed - totalReturned;

            // Find the nearest due date
            LocalDate nearestDueDate = transactions.stream()
                    .filter(t -> !"Returned".equalsIgnoreCase(t.getStatus())) // Only pending returns
                    .map(BorrowReturn::getReturnDate)
                    .filter(date -> date != null)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            // Update labels
            totalBorrowedLabel.setText(String.valueOf(totalBorrowed));
            totalReturnedLabel.setText(String.valueOf(totalReturned));
            pendingReturnsLabel.setText(String.valueOf(pendingReturns));

            // Load rank image
            String rankImagePath = getRankImagePath(totalBorrowed);
            Image rankImage = new Image(getClass().getResourceAsStream(rankImagePath));
            if (rankImage.isError()) {
                showError("Unable to load image for rank at path: " + rankImagePath);
            } else {
                rankImageView.setImage(rankImage);
            }

        } catch (SQLException e) {
            showError("Error while updating dashboard: " + e.getMessage());
        }
    }
    // Hàm trả về đường dẫn của ảnh rank dựa trên tổng sách đã mượn
    private String getRankImagePath(long totalBorrowed) {
        if (totalBorrowed < 10) {
            return "/icons/rank1.png";
        } else if (totalBorrowed < 20) {
            return "/icons/rank2.png";
        } else if (totalBorrowed < 50) {
            return "/icons/rank3.png";
        } else if (totalBorrowed < 100) {
            return "/icons/rank4.png";
        } else {
            return "/icons/rank5.png";
        }
    }

}
