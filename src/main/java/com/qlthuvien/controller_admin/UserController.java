package com.qlthuvien.controller_admin;

import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.StarAnimationUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserController {

    private Connection connection;
    private UserDAO userDAO;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> phoneColumn;
    @FXML
    private TableColumn<User, String> membershipIdColumn;
    @FXML
    private TableColumn<User, String> passwordColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private VBox starContainer;
    @FXML
    private TextField nameInput, emailInput, phoneInput, membershipIdInput, passwordInput, usernameInput;

    @FXML
    private Label statusLabel;

    public UserController() {
        connection = DBConnection.getConnection();
        userDAO = new UserDAO(connection);
    }

    @FXML
    public void initialize() {
        // Bind column widths to table view width
        nameColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.15));
        emailColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.15));
        phoneColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.15));
        membershipIdColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.15));
        usernameColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.15));
        passwordColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.15));

        // Set cell value factories for columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("user_name"));

        // Load data into the table
        refreshUsersTable();

        // Handle row selection
        usersTable.setOnMouseClicked(event -> {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                nameInput.setText(selectedUser.getName());
                emailInput.setText(selectedUser.getEmail());
                phoneInput.setText(selectedUser.getPhone());
                membershipIdInput.setText(selectedUser.getMembershipId());
                usernameInput.setText(selectedUser.getUser_name());
                passwordInput.setText(selectedUser.getPassword());
            }
        });

        // create star animation

        if (starContainer != null) {
            Platform.runLater(() -> {
                StarAnimationUtil.createStarAnimation(starContainer);
            });
        }
    }

    @FXML
    public void addUser() {
        try {
            User newUser = new User(
                    membershipIdInput.getText(),
                    nameInput.getText(),
                    emailInput.getText(),
                    phoneInput.getText(),
                    usernameInput.getText(),
                    passwordInput.getText()
            );
            userDAO.add(newUser);
            showSuccess("User added successfully!");
            refreshUsersTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void editUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("No user selected");
            return;
        }

        try {
            selectedUser.setName(nameInput.getText());
            selectedUser.setEmail(emailInput.getText());
            selectedUser.setPhone(phoneInput.getText());
            selectedUser.setUser_name(usernameInput.getText());
            selectedUser.setPassword(passwordInput.getText());
            userDAO.update(selectedUser);
            showSuccess("User updated successfully!");
            refreshUsersTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void deleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("No user selected");
            return;
        }

        try {
            userDAO.delete(selectedUser.getMembershipId());
            showSuccess("User deleted successfully!");
            refreshUsersTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void refreshUsersTable() {
        try {
            // Lấy danh sách toàn bộ người dùng từ cơ sở dữ liệu
            List<User> users = userDAO.getAll();
            usersTable.getItems().setAll(users); // Hiển thị danh sách trong bảng
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
