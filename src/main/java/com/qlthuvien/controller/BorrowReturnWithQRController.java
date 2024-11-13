package com.qlthuvien.controller;

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

    /**
     * Constructor BorrowReturnWithQRController.
     * Khởi tạo kết nối của database và các đối tượng.
     */
    public BorrowReturnWithQRController() {
        connection = DBConnection.getConnection();
        borrowReturnDAO = new BorrowReturnDAO(connection);
        userDAO = new UserDAO(connection);
    }

    /**
     * Phương thức import QR code.
     */
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
                        // Hiển thị thông tin cho người dùng
                        membershipIdInput.setText(user.getMembershipId());
                        membershipIdInput.setDisable(true);
                        checkButton.setDisable(true);
                        documentStatusLabel.setText("Borrowed by: " + user.getName());
                        submitButton.setText("Return");
                    } else {
                        // Báo lỗi thông tin mượn chưa được tìm thấy
                    }
                }
                submitButton.setDisable(false);
            } catch (Exception e) {
                // Báo lỗi chưa có thể đọc được QR Code
            }
        }
    }


}
