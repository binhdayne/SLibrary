package com.qlthuvien.login;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class FirstController implements Initializable {

    @FXML
    private Button button_manager_login;

    @FXML
    private Button button_user_login;

    @FXML
    private TextField tf_manager_password;

    @FXML
    private TextField tf_manager_username;

    @FXML
    private TextField tf_user_password;

    @FXML
    private TextField tf_user_username;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_manager_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Login_Utils.LoginManager(event, tf_manager_username.getText(), tf_manager_password.getText());
            }
        });

        button_user_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Login_Utils.LoginUser(event, tf_user_password.getText(), tf_user_username.getText());
            }
        });
    }
}
