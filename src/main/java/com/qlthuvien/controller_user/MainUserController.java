package com.qlthuvien.controller_user;

import com.qlthuvien.model.User;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

public class MainUserController {

    @FXML
    private VBox contentArea;

    @FXML
    private Button btnHome, btnUsers, btnSettings, btnDocuments;

    @FXML
    private Label userIdLabel, nameLabel, emailLabel, phoneLabel, userNameLabel;  // Label to display the user ID


    private String membershipId, name, phone, email;

    public void setmembershipId(String membershipId) {
        userIdLabel.setText(membershipId);
    }

    public String getUserId() {
        return userIdLabel.getText();
    }


    private Button activeButton; // Nút đang được chọn


    private void fadeInContent() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), contentArea);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    @FXML
    public void initialize() {
        setActiveButton(btnHome);
    }

    // Phương thức để đặt nút hiện tại là active và thay đổi giao diện
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active-button");
        }
        button.getStyleClass().add("active-button");
        activeButton = button;
    }

    // Sự kiện khi nhấn nút Home
    @FXML
    public void showHomeScreen() {
        setActiveButton(btnHome);
        contentArea.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/giao_dien_nguoi_dung/HomeTab_user.fxml"));
            Node borrowBookScreen = loader.load();

            // Lấy controller từ loader
            HomeTabUser borrowBookController = loader.getController();

            // Truyền userId từ MainUserController sang BorrowBookController
            String currentUserId = getUserId(); // Sử dụng phương thức getter
            borrowBookController.setUserId(currentUserId);

            // Thêm giao diện BorrowBook vào contentArea
            contentArea.getChildren().add(borrowBookScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Sự kiện khi nhấn nút Documents
    @FXML
    private void showDocumentsScreen() {
        setActiveButton(btnDocuments);
        contentArea.getChildren().clear();
        try {
            // Tải FXML của DocumentManagement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/giao_dien_nguoi_dung/DocumentManagement.fxml"));
            Node borrowBookScreen = loader.load();

            // Lấy controller từ FXMLLoader
            DocumentManagementController documentController = loader.getController();

            // Gán userId vào controller
            String currentUserId = getUserId(); // Hàm này trả về userId thực tế của người dùng
            if (currentUserId != null && !currentUserId.isEmpty()) {
                documentController.setUserId(currentUserId); // Truyền userId
            } else {
                System.err.println("Error: User ID is null or empty.");
            }

            // Thêm giao diện DocumentManagement vào contentArea
            contentArea.getChildren().add(borrowBookScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public void setname(String name) {
//        this.nameLabel = name;
//        nameLabel.setText(name);
//    }
    // Sự kiện khi nhấn nút Users
@FXML
public void showProfileScreen() {
    setActiveButton(btnUsers);
    contentArea.getChildren().clear();
    try {
        // Tạo FXMLLoader và tải FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/giao_dien_nguoi_dung/Profile.fxml"));
        Node userManagement = loader.load();

        // Lấy controller từ loader
        ProfileController profileController = loader.getController();

        // Truyền userId vào controller (giả sử userId là một biến đã được lưu trong MainController)
        String currentUserId = userIdLabel.getText(); // Thay giá trị này bằng ID người dùng thực tế
        profileController.setUserId(currentUserId);

        // Thêm giao diện profile vào contentArea
        contentArea.getChildren().add(userManagement);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

//    // Sự kiện khi nhấn nút Loans
//    @FXML
//    public void showLoansScreen() {
//        setActiveButton(btnLoans);
//        contentArea.getChildren().clear();
//        try {
//            Node borrowReturnManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/BorrowReturnManagement.fxml"));
//            contentArea.getChildren().add(borrowReturnManagement);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
    // Sự kiện khi nhấn nút Settings
    @FXML
    public void showSettingsScreen() {
        setActiveButton(btnSettings);
        contentArea.getChildren().clear();
        Label settingsLabel = new Label("This is the Settings Screen");
        settingsLabel.setStyle("-fx-font-size: 24px;");
        contentArea.getChildren().add(settingsLabel);
    }

    public void editUser(ActionEvent actionEvent) {
    }


//
    @FXML
    public void showLogoutScreen() {
        try {
            // Tải giao diện đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/FirstScreen.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Lấy Stage hiện tại và thay đổi scene sang màn hình đăng nhập
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
