package com.qlthuvien.controller_admin;

import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private TextField nameInput, emailInput, phoneInput, membershipIdInput;

    @FXML
    private Label statusLabel;

    public UserController() {
        connection = DBConnection.getConnection();
        userDAO = new UserDAO(connection);
    }

    @FXML
    public void initialize() {
        // Đặt tỷ lệ chiều rộng cho các cột dựa trên tổng chiều rộng của bảng
        nameColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.3));
        emailColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.3));
        phoneColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.2));
        membershipIdColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.2));
        // Liên kết các cột với thuộc tính của User
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));

        refreshUsersTable();

        // Xử lý sự kiện khi nhấn vào một dòng trong bảng
        usersTable.setOnMouseClicked(event -> {
            User selectedUser = usersTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                nameInput.setText(selectedUser.getName());
                emailInput.setText(selectedUser.getEmail());
                phoneInput.setText(selectedUser.getPhone());
                membershipIdInput.setText(selectedUser.getMembershipId());
            }
        });
    }

    @FXML
    public void addUser() {
        try {
            User newUser = new User(membershipIdInput.getText(), nameInput.getText(), emailInput.getText(),
                    phoneInput.getText());
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
            List<User> users = userDAO.getAll();
            usersTable.getItems().setAll(users);
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
