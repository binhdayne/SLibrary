package com.example.login;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));
        stage.setTitle("Log In !");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
    public static void main(String[] args) {launch(args);}
}
