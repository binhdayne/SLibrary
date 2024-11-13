package com.qlthuvien.controller;

import com.qlthuvien.dao.BorrowReturnDAO;
import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.model.BorrowReturn;
import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BorrowReturnNormalController {

    @FXML
    private TableView<BorrowReturn> borrowReturnTable;

    @FXML
    private TableColumn<BorrowReturn, String> membershipIdColumn;
    @FXML
    private TableColumn<BorrowReturn, String> documentIdColumn;
    @FXML
    private TableColumn<BorrowReturn, String> documentTypeColumn;
    @FXML
    private TableColumn<BorrowReturn, LocalDate> borrowDateColumn;
    @FXML
    private TableColumn<BorrowReturn, LocalDate> returnDateColumn;
    @FXML
    private TableColumn<BorrowReturn, String> statusColumn;

    @FXML
    private TextField membershipIdInput, documentIdInput;
    @FXML
    private ComboBox<String> documentTypeInput;
    @FXML
    private Label nameLabel, emailLabel, phoneLabel, documentDetailsLabel;
    @FXML
    private DatePicker borrowDateInput, returnDateInput;
    @FXML
    private Button borrowButton, returnButton;
    @FXML
    private GridPane documentFormPane;

    private Connection connection;
    private BorrowReturnDAO borrowReturnDAO;
    private UserDAO userDAO;

    /**
     * Constructor BorrowReturnNormalController.
     * Khởi tạo kết nối của database và các đối tượng.
     */
    public BorrowReturnNormalController() {
        connection = DBConnection.getConnection();
        borrowReturnDAO = new BorrowReturnDAO(connection);
        userDAO = new UserDAO(connection);
    }


    @FXML
    public void initialize() {
        // Khởi tạo 1 gói combo bao gồm các kiểu document
        ObservableList<String> documentTypes = FXCollections.observableArrayList("BOOK", "MAGAZINE", "THESIS", "BOOK_FROM_API");
        documentTypeInput.setItems(documentTypes);

        // Đặt chiều rộng cột và ràng buộc những thuộc tính
        membershipIdColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        documentIdColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        documentTypeColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        borrowDateColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        returnDateColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        statusColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));

        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        documentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load dữ liệu ban đầu từ cơ sở dữ liệu
    }



}
