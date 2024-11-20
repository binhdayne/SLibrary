package com.qlthuvien.controller_admin;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private VBox contentArea;

    @FXML
    private Button btnHome, btnDocuments, btnUsers, btnLoans, btnSettings, btnlogout;

    private Button activeButton; // Nút đang được chọn


//    private void fadeInContent() {
//        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), contentArea);
//        fadeTransition.setFromValue(0);
//        fadeTransition.setToValue(1);
//        fadeTransition.play();
//    }

    /**
     * Initializes the controller.
     * Sets the Home button as the default active button when the application starts.
     */
    @FXML
    public void initialize() {
        setActiveButton(btnHome);
    }

    /**
     * Sets the visual state of the active navigation button.
     * Removes the active style from the previous button and applies it to the new one.
     *
     * @param button The button to be set as active
     */
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active-button");
        }
        button.getStyleClass().add("active-button");
        activeButton = button;
    }

    /**
     * Displays the Home screen in the content area.
     * Loads and displays the HomeScreen.fxml content.
     * Sets the Home button as active in the navigation.
     */
    @FXML
    public void showHomeScreen() {
        setActiveButton(btnHome);
        contentArea.getChildren().clear();
        try {
            Node documentManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/HomeScreen.fxml"));
            contentArea.getChildren().add(documentManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the Documents management screen in the content area.
     * Loads and displays the DocumentManagement.fxml content.
     * Sets the Documents button as active in the navigation.
     */
    @FXML
    public void showDocumentsScreen() {
        setActiveButton(btnDocuments);
        contentArea.getChildren().clear();
        try {
            Node documentManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/DocumentManagement.fxml"));
            contentArea.getChildren().add(documentManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the Users management screen in the content area.
     * Loads and displays the UserManagement.fxml content.
     * Sets the Users button as active in the navigation.
     */
    @FXML
    public void showUsersScreen() {
        setActiveButton(btnUsers);
        contentArea.getChildren().clear();
        try {
            Node userManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/UserManagement.fxml"));
            contentArea.getChildren().add(userManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the Loans management screen in the content area.
     * Loads and displays the BorrowReturnManagement.fxml content.
     * Sets the Loans button as active in the navigation.
     */
    @FXML
    public void showLoansScreen() {
        setActiveButton(btnLoans);
        contentArea.getChildren().clear();
        try {
            Node borrowReturnManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/BorrowReturnManagement.fxml"));
            contentArea.getChildren().add(borrowReturnManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the Settings screen in the content area.
     * Creates and displays a simple label for the Settings screen.
     * Sets the Settings button as active in the navigation.
     */
    @FXML
    public void showSettingsScreen() {
        setActiveButton(btnSettings);
        contentArea.getChildren().clear();
        Label settingsLabel = new Label("This is the Settings Screen");
        settingsLabel.setStyle("-fx-font-size: 24px;");
        contentArea.getChildren().add(settingsLabel);
    }

    /**
     * Handles the logout process.
     * Returns to the login screen (FirstScreen.fxml) when the logout button is clicked.
     * Closes the current session and displays the login interface.
     */
    @FXML
    public void showLogoutScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/FirstScreen.fxml"));
            Scene loginScene = new Scene(loader.load());
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
