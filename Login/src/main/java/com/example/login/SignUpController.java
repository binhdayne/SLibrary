package com.example.login;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_password;

    @FXML
    private Button button_signup;
    @FXML
    private Button button_log_in;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_signup.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (!tf_username.getText().trim().isEmpty() && !tf_password.getText().trim().isEmpty()) {
                    DBUtils.signUpUser(actionEvent, tf_username.getText(), tf_password.getText());
                } else {
                    System.out.println("Please enter all fields");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Please enter all information to sign up, mate !");
                    alert.show();
                }
            }
        });

        button_log_in.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DBUtils.changeScene(actionEvent,"com/example/login/Sample.fxml","Log In!");
            }
        });
    }
}
