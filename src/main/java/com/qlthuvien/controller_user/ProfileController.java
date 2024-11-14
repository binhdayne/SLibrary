package com.qlthuvien.controller_user;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.util.Map;
import com.qlthuvien.login.Login_Utils ;

public class ProfileController {

    @FXML
    private Label userIdLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label userNameLabel;

    // Phương thức để nhận và hiển thị membershipId
    public void setMembershipId(String membershipId) {
        userIdLabel.setText("User ID: " + membershipId);

        // Lấy thông tin người dùng từ Login_Utils
        Map<String, String> userInfo = Login_Utils.getUserInfo(membershipId);

        // Hiển thị thông tin người dùng nếu có
        if (userInfo != null) {
            nameLabel.setText("Name: " + userInfo.get("name"));
            emailLabel.setText("Email: " + userInfo.get("email"));
            phoneLabel.setText("Phone: " + userInfo.get("phone"));
            userNameLabel.setText("Username: " + userInfo.get("user_name"));
        }
    }
}
