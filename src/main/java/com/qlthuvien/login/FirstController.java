package com.qlthuvien.login;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class FirstController implements Initializable {

    @FXML
    private Button button_manager_login;

    @FXML
    private Button button_user_login;

    @FXML
    private PasswordField tf_manager_password;

    @FXML
    private TextField tf_manager_password_visible;

    @FXML
    private TextField tf_manager_username;

    @FXML
    private PasswordField tf_user_password;

    @FXML
    private TextField tf_user_password_visible;

    @FXML
    private TextField tf_user_username;

    @FXML
    private CheckBox cb_show_manager_password;

    @FXML
    private CheckBox cb_show_user_password;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Đồng bộ dữ liệu giữa PasswordField và TextField cho Manager
        tf_manager_password_visible.textProperty().bindBidirectional(tf_manager_password.textProperty());
        tf_manager_password_visible.visibleProperty().bind(cb_show_manager_password.selectedProperty());
        tf_manager_password.visibleProperty().bind(cb_show_manager_password.selectedProperty().not());

        // Đồng bộ dữ liệu giữa PasswordField và TextField cho User
        tf_user_password_visible.textProperty().bindBidirectional(tf_user_password.textProperty());
        tf_user_password_visible.visibleProperty().bind(cb_show_user_password.selectedProperty());
        tf_user_password.visibleProperty().bind(cb_show_user_password.selectedProperty().not());

        // Xử lý sự kiện nút đăng nhập cho Manager
        button_manager_login.setOnAction((ActionEvent event) -> {
            String managerPassword = Bindings.when(cb_show_manager_password.selectedProperty())
                    .then(tf_manager_password_visible.textProperty())
                    .otherwise(tf_manager_password.textProperty())
                    .getValue();
            Login_Utils.LoginManager(event, tf_manager_username.getText(), managerPassword);
        });

        // Xử lý sự kiện nút đăng nhập cho User
        button_user_login.setOnAction((ActionEvent event) -> {
            String userPassword = Bindings.when(cb_show_user_password.selectedProperty())
                    .then(tf_user_password_visible.textProperty())
                    .otherwise(tf_user_password.textProperty())
                    .getValue();
            Login_Utils.LoginUser(event, tf_user_username.getText(), userPassword);
        });
    }
}
