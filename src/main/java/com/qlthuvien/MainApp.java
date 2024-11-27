package com.qlthuvien;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Tải file fxml
        Parent root = FXMLLoader.load(getClass().getResource("/com/qlthuvien/view_manager/FirstScreen.fxml"));

        // Đặt tên cho phần mềm
        primaryStage.setTitle("SLibrary Login");

        // Đặt kích thước
        primaryStage.setScene(new Scene(root, 835, 520));

        // Load logo cho phần mềm
        Image logo = new Image(getClass().getResourceAsStream("/icons/logo.jpg"));
        primaryStage.getIcons().add(logo);
        primaryStage.show();
    }

    // Chạy.
    public static void main(String[] args) {
        launch(args);
    }
}