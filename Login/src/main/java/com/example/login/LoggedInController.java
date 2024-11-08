package com.example.login;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


import java.net.URL;
import java.util.ResourceBundle;

public class LoggedInController implements Initializable {

    @FXML
    private Button buttton_logout;
    @FXML
    private Button button_books;
    @FXML
    private Label label_welcome;
    @FXML
    private Label label_levelshow;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        buttton_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
             DBUtils.changeScene(event,"com/example/login/Sample.fxml", "Log in !");
            }
        });

        button_books.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DBUtils.changeScene(event,"com/example/login/Books.fxml", "Log in !");
            }
        });

    }

    public void setUserInformation(String username) {
        label_welcome.setText("Welcome " + username + " !");
        label_levelshow.setText("Level Show of " + username + " is beginner");
    }


}
