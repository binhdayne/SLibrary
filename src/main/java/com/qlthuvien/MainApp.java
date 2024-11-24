package com.qlthuvien;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Fixing URL which is suitable for view_manager.
        Parent root = FXMLLoader.load(getClass().getResource("/com/qlthuvien/view_manager/FirstScreen.fxml"));
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(new Scene(root, 835, 520));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

