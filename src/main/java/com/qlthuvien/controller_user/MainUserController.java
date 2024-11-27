package com.qlthuvien.controller_user;
import com.qlthuvien.game.MillionaireGameController;
import com.qlthuvien.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;

import com.qlthuvien.model.User;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MainUserController extends BaseController{

    @FXML
    private VBox contentArea;
    @FXML
    private Button btnGame;

    @FXML
    private Button btnHome, btnUsers, btnSettings, btnDocuments, btnfirstScreen;

    @FXML
    private Label userIdLabel, userNameLabel;

    @FXML
    private ImageView timeImageView, avatarImageView;

    private String membershipId, name, phone, email, avatar;

    public String getUserId() {
        return userIdLabel.getText();
    }

    public void setmembershipId(String membershipId) {
        this.membershipId = membershipId;
        userIdLabel.setText(membershipId);

        // Fetch user information from the database
        try {
            User user = getUserFromDatabase(membershipId);
            if (user != null) {
                // Set user name and avatar
                userNameLabel.setText(user.getName());
                setAvatar(user.getAvatar());
            } else {
                // Default values if user is not found
                userNameLabel.setText("Unknown User");
                setAvatar(null);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user data: " + e.getMessage());
            userNameLabel.setText("Error loading user");
            setAvatar(null);
        }
    }

    private User getUserFromDatabase(String membershipId) throws SQLException {
        String query = "SELECT membership_id, name, email, phone, avatar FROM users WHERE membership_id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, membershipId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("membership_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("avatar") // Đường dẫn avatar
                    );
                }
            }
        }
        return null;
    }

    public void setAvatar(String avatarPath) {
        if (avatarPath != null && !avatarPath.isEmpty()) {
            try {
                // Tạo Image từ đường dẫn avatarPath
                Image avatarImage = new Image(new File(avatarPath).toURI().toString());
                avatarImageView.setImage(avatarImage);
            } catch (Exception e) {
                // Hiển thị ảnh mặc định nếu xảy ra lỗi
                avatarImageView.setImage(new Image(getClass().getResourceAsStream("/icons/default.jpg")));
                System.err.println("Error loading avatar image: " + e.getMessage());
            }
        } else {
            // Hiển thị ảnh mặc định nếu avatarPath rỗng hoặc null
            avatarImageView.setImage(new Image(getClass().getResourceAsStream("/icons/default.jpg")));
        }
    }



    private Button activeButton;

    private void fadeInContent() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), contentArea);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

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
//        btnGame.setOnAction(event -> startGame());
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


    @FXML
    public void showFirstScreen() {
        setActiveButton(btnfirstScreen);
        contentArea.getChildren().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/giao_dien_nguoi_dung/FirstScreen.fxml"));
            VBox firstScreen = loader.load();

            // Get the FirstScreenController instance
            FirstScreenController firstScreenController = loader.getController();

            // Pass the user ID to the FirstScreenController
            String currentUserId = getUserId();
            firstScreenController.setUserDetails(currentUserId);

            // Add the FirstScreen content to the contentArea
            contentArea.getChildren().add(firstScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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

    @FXML
    public void showLogoutScreen() {
        try {
            // Tải giao diện đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/view_manager/FirstScreen.fxml"));
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
    private void onGameButtonClicked(ActionEvent event) {
        setActiveButton(btnGame);
        contentArea.getChildren().clear();
        try {
            // Tạo FXMLLoader và tải FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/game/MillionaireGame.fxml"));
            Node userManagement = loader.load();

            // Lấy controller từ loader
            MillionaireGameController profileController = loader.getController();


            // Thêm giao diện profile vào contentArea
            contentArea.getChildren().add(userManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
