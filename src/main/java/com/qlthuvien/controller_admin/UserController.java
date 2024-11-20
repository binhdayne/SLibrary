package com.qlthuvien.controller_admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;


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

    /**
     * Constructor for UserController.
     * Initializes database connection and UserDAO instance for database operations.
     */
    public UserController() {
        connection = DBConnection.getConnection();
        userDAO = new UserDAO(connection);
    }

    /**
     * Initializes the controller and sets up the TableView components.
     * - Sets column widths as percentages of table width
     * - Configures cell value factories for each column
     * - Sets up row selection handler to populate input fields
     * - Loads initial user data into the table
     */
    @FXML
    public void initialize() {
        // Set column widths based on table size
        nameColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.3));
        emailColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.3));
        phoneColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.2));
        membershipIdColumn.prefWidthProperty().bind(usersTable.widthProperty().multiply(0.2));
        // Link columns to User properties
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));

        refreshUsersTable();

        // Handle event when clicking on a row in the table
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

    /**
     * Adds a new user to the library system.
     * Creates a new User object using the values from input fields:
     * - Membership ID
     * - Name
     * - Email
     * - Phone number
     * Updates the table view and displays a success message if successful.
     * 
     * @throws SQLException if there is an error adding the user to the database
     */
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

    /**
     * Updates the selected user's information in the database.
     * Updates the following fields from input values:
     * - Name
     * - Email
     * - Phone number
     * Displays error if no user is selected.
     * Refreshes table view after successful update.
     * 
     * @throws SQLException if there is an error updating the user in the database
     */
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

    /**
     * Deletes the selected user from the library system.
     * Removes user record based on membership ID.
     * Displays error if no user is selected.
     * Refreshes table view after successful deletion.
     * 
     * @throws SQLException if there is an error deleting the user from the database
     */
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

    /**
     * Refreshes the users table with current data from the database.
     * Retrieves all users and updates the TableView content.
     * Displays error message if database operation fails.
     * 
     * @throws SQLException if there is an error retrieving users from the database
     */
    private void refreshUsersTable() {
        try {
            List<User> users = userDAO.getAll();
            usersTable.getItems().setAll(users);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Displays an error message dialog to the user.
     * Creates and shows an error alert with the specified message.
     * 
     * @param message The error message to be displayed in the alert dialog
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Displays a success message dialog to the user.
     * Creates and shows an information alert with the specified message.
     * 
     * @param message The success message to be displayed in the alert dialog
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
