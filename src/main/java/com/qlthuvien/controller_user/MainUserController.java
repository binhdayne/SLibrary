package com.qlthuvien.controller_user;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalTime;
import com.qlthuvien.game.SnakeGame;
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
    private Button btnGame;

    @FXML
    private Button btnHome, btnUsers, btnSettings, btnDocuments;

    @FXML
    private Label userIdLabel, nameLabel, emailLabel, phoneLabel, userNameLabel;  // Label to display the user ID

    @FXML
    private ImageView timeImageView;

    private String membershipId, name, phone, email;

    public String getUserId() {
        return userIdLabel.getText();
    }

    public void setmembershipId(String membershipId) {
        userIdLabel.setText(membershipId);
    }
//    public void setname(String name) {
//        nameLabel.setText(name);
//    }
//
//    public void setemail(String email) {
//        emailLabel.setText(email);
//    }
//
//    public void setphone(String phone) {
//        phoneLabel.setText(phone);
//    }

    private Button activeButton; // Nút đang được chọn


//    private void fadeInContent() {
//        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), contentArea);
//        fadeTransition.setFromValue(0);
//        fadeTransition.setToValue(1);
//        fadeTransition.play();
//    }
private String getImagePath(LocalTime time) {
    if (time.isAfter(LocalTime.of(4, 59)) && time.isBefore(LocalTime.of(12, 0))) {
        return getClass().getResource("/icons/Gmning.gif").toExternalForm();
    } else if (time.isAfter(LocalTime.of(11, 59)) && time.isBefore(LocalTime.of(18, 0))) {
        return getClass().getResource("/icons/Gatn.gif").toExternalForm();
    } else if (time.isAfter(LocalTime.of(17, 59)) && time.isBefore(LocalTime.of(23, 0))) {
        return getClass().getResource("/icons/Gev.gif").toExternalForm();
    } else {
        return getClass().getResource("/icons/Gn.gif").toExternalForm();
    }
}
    @FXML
    public void initialize() {
        setActiveButton(btnHome);
        btnGame.setOnAction(event -> launchSnakeGame());
        // Lấy thời gian hiện tại
        LocalTime now = LocalTime.now();

        // Lấy đường dẫn ảnh dựa trên khung giờ
        String imagePath = getImagePath(now);

        // Hiển thị ảnh trong ImageView
        Image image = new Image(imagePath);
        timeImageView.setImage(image);
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


//    public void setMembershipId(String membershipId) {
//        this.membershipId = membershipId;
//        userIdLabel.setText("User ID: " + membershipId);
//    }

//    public void setname(String name) {
//        this.nameLabel = name;
//        nameLabel.setText(name);
//    }
    // Sự kiện khi nhấn nút Users
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
    @FXML
    public void launchSnakeGame() {
        try {
            // Tạo một Stage mới mỗi khi nhấn nút
            Stage gameStage = new Stage();

            // Khởi động SnakeGame
            SnakeGame snakeGame = new SnakeGame();

            // Reset trạng thái game
            snakeGame.resetGameState(); // Hàm để reset trạng thái (thêm ở bên dưới)

            snakeGame.start(gameStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
