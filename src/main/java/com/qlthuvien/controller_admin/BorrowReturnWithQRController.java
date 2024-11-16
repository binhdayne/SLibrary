package com.qlthuvien.controller_admin;

import com.qlthuvien.dao.BorrowReturnDAO;
import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.model.BorrowReturn;
import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.QRCodeDecoder;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class BorrowReturnWithQRController {

    @FXML
    private Label qrContentLabel, documentStatusLabel, userNameLabel, userPhoneLabel, userEmailLabel;
    @FXML
    private TextField membershipIdInput;
    @FXML
    private Button submitButton, checkButton;

    private Connection connection;
    private BorrowReturnDAO borrowReturnDAO;
    private UserDAO userDAO;
    private String documentType;
    private int documentId;
    private String currentStatus;

    public BorrowReturnWithQRController() {
        connection = DBConnection.getConnection();
        borrowReturnDAO = new BorrowReturnDAO(connection);
        userDAO = new UserDAO(connection);
    }

    @FXML
    private void importQRCode() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open QR Code Image");
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                String qrContent = QRCodeDecoder.decodeQRCode(file);
                qrContentLabel.setText(qrContent);

                // Phân tích nội dung mã QR
                String[] parts = qrContent.split(", ");
                documentType = parts[0].split(": ")[1];
                documentId = Integer.parseInt(parts[1].split(": ")[1]);

                // Kiểm tra trạng thái tài liệu
                currentStatus = borrowReturnDAO.getDocumentStatus(documentType, documentId);
                documentStatusLabel.setText(currentStatus);

                // Nếu tài liệu có sẵn
                if ("available".equals(currentStatus)) {
                    membershipIdInput.setDisable(false);
                    checkButton.setDisable(false);
                    submitButton.setText("Borrow");
                }
                // Nếu đã cho mượn
                else if ("borrowed".equals(currentStatus)) {
                    BorrowReturn borrowInfo = borrowReturnDAO.getBorrowInfo(documentType, documentId);
                    if (borrowInfo != null) {
                        User user = userDAO.findById(borrowInfo.getMembershipId());
                        displayUserInfo(user);
                        membershipIdInput.setText(user.getMembershipId());
                        membershipIdInput.setDisable(true);
                        checkButton.setDisable(true);
                        documentStatusLabel.setText("Borrowed by: " + user.getName());
                        submitButton.setText("Return");
                    } else {
                        showError("Borrow information not found.");
                    }
                }
                submitButton.setDisable(false);
            } catch (Exception e) {
                showError("Error reading QR code: " + e.getMessage());
            }
        }
    }

    @FXML
    private void checkMembershipId() {
        try {
            String membershipId = membershipIdInput.getText();
            User user = userDAO.findById(membershipId);
            if (user != null) {
                displayUserInfo(user);
                submitButton.setDisable(false);
            } else {
                showError("User not found.");
            }
        } catch (SQLException e) {
            showError("Error checking membership ID: " + e.getMessage());
        }
    }

    @FXML
    private void processTransaction() {
        try {
            if ("available".equals(currentStatus)) {
                String membershipId = membershipIdInput.getText();
                if (membershipId.isEmpty()) {
                    showError("Please enter membership ID.");
                    return;
                }
                // Xử lý cho mượn
                BorrowReturn newBorrow = new BorrowReturn(
                        membershipId,
                        documentId,
                        documentType,
                        LocalDate.now(),
                        null,
                        "Borrowed"
                );
                borrowReturnDAO.borrowDocument(newBorrow);
                showSuccess("Document borrowed successfully!");
            } else if ("borrowed".equals(currentStatus)) {
                // Xử lý trả tài liệu
                BorrowReturn borrowInfo = borrowReturnDAO.getBorrowInfo(documentType, documentId);
                borrowInfo.setReturnDate(LocalDate.now());
                borrowInfo.setStatus("Returned");
                borrowReturnDAO.returnDocument(borrowInfo);
                showSuccess("Document returned successfully!");
            }
        } catch (SQLException e) {
            showError("Error processing transaction: " + e.getMessage());
        }
    }

    private void displayUserInfo(User user) {
        userNameLabel.setText(user.getName());
        userPhoneLabel.setText(user.getPhone());
        userEmailLabel.setText(user.getEmail());
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
}
