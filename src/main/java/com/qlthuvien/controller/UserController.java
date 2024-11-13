package com.qlthuvien.controller;

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

    /**
     * Constructor UserController.
     * Khởi tạo kết nối của database và đối tượng UserDAO.
     */
    public UserController() {
        connection = DBConnection.getConnection();
        userDAO = new UserDAO(connection);
    }

    /**
     * Khởi tạo giao diện người dùng.
     */
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

        // Load dữ liệu ban đầu từ cơ sở dữ liệu

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
}
