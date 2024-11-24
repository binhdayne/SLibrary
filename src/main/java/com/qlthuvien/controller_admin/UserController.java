package com.qlthuvien.controller_admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class UserController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, String> membershipIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> passwordColumn;

    @FXML private TextField nameInput;
    @FXML private TextField emailInput;
    @FXML private TextField phoneInput;
    @FXML private TextField membershipIdInput;
    @FXML private TextField usernameInput;
    @FXML private TextField passwordInput;
    @FXML private TextField searchField;

    private UserDAO userDAO;
    private List<User> allUsers;

    @FXML
    public void initialize() {
        try {
            Connection connection = DBConnection.getConnection();
            userDAO = new UserDAO(connection);

            // Thiết lập các cột
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone")); 
            membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("user_name"));
            passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

            // Thêm listener cho searchField
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                handleSearch();
            });

            // Xử lý sự kiện khi chọn user trong bảng
            usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    nameInput.setText(newSelection.getName());
                    emailInput.setText(newSelection.getEmail());
                    phoneInput.setText(newSelection.getPhone());
                    membershipIdInput.setText(newSelection.getMembershipId());
                    usernameInput.setText(newSelection.getUser_name());
                    passwordInput.setText(newSelection.getPassword());
                }
            });

            refreshUsersTable();
        } catch (Exception e) {
            showError("Error initializing: " + e.getMessage());
        }
    }

    @FXML
    public void addUser() {
        try {
            // Validate input
            if (membershipIdInput.getText().isEmpty() || nameInput.getText().isEmpty() || 
                usernameInput.getText().isEmpty() || passwordInput.getText().isEmpty()) {
                showError("Please fill in all required fields");
                return;
            }

            User newUser = new User(
                membershipIdInput.getText(),
                nameInput.getText(),
                emailInput.getText(),
                phoneInput.getText(),
                passwordInput.getText(),
                usernameInput.getText()
            );

            userDAO.add(newUser);
            showSuccess("User added successfully!");
            clearInputs();
            refreshUsersTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void editUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("Please select a user to edit");
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
            showError("Please select a user to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setContentText("Are you sure you want to delete this user?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                userDAO.delete(selectedUser.getMembershipId());
                showSuccess("User deleted successfully!");
                clearInputs();
                refreshUsersTable();
            } catch (SQLException e) {
                showError(e.getMessage());
            }
        }
    }

    private void clearInputs() {
        nameInput.clear();
        emailInput.clear();
        phoneInput.clear();
        membershipIdInput.clear();
        usernameInput.clear();
        passwordInput.clear();
    }

    private void refreshUsersTable() {
        try {
            List<User> users = userDAO.getAll();
            usersTable.getItems().setAll(users);
        } catch (SQLException e) {
            showError("Error refreshing table: " + e.getMessage());
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

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            usersTable.getItems().setAll(allUsers); // Hiển thị lại toàn bộ danh sách
            return;
        }

        // Lọc danh sách users dựa trên searchText
        List<User> filteredUsers = allUsers.stream()
            .filter(user -> 
                user.getName().toLowerCase().contains(searchText) ||
                user.getEmail().toLowerCase().contains(searchText) ||
                user.getPhone().toLowerCase().contains(searchText) ||
                user.getMembershipId().toLowerCase().contains(searchText) ||
                user.getUser_name().toLowerCase().contains(searchText)
            )
            .collect(Collectors.toList());

        usersTable.getItems().setAll(filteredUsers);
    }
}
