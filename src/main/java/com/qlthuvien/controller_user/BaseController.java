package com.qlthuvien.controller_user;

import javafx.scene.control.Alert;

public abstract class BaseController {

    // Phương thức để hiển thị thông báo lỗi
    void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }


    // Phương thức để hiển thị thông báo thành công
    void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }


    // Phương thức để hiển thị thông tin
    void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Phương thức để hiển thị tin nhắn
    void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
