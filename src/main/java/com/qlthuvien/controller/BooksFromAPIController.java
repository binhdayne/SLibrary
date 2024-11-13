package com.qlthuvien.controller;

import com.qlthuvien.api.GoogleBooksAPI;
import com.qlthuvien.dao.BookFromAPIDAO;
import com.qlthuvien.model.BookFromAPI;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class BooksFromAPIController {

    @FXML
    private TextField isbnInput, titleInput, authorInput, publisherInput, publishedDateInput, descriptionInput;
    @FXML
    private TableView<BookFromAPI> booksTable;
    @FXML
    private TableColumn<BookFromAPI, Integer> idColumn;
    @FXML
    private TableColumn<BookFromAPI, String> isbnColumn, titleColumn, authorColumn, publisherColumn, publishedDateColumn, statusColumn;
    @FXML
    private Label statusApiLabel, statusLoadDataLabel;

    private BookFromAPIDAO bookFromAPIDAO;

    /**
     * Constructor BooksFromAPIController.
     * Khởi tạo kết nối database và đối tượng BookFromAPIController.
     */
    public BooksFromAPIController() {
        Connection connection = DBConnection.getConnection();  // Lấy connection từ DBConnection
        this.bookFromAPIDAO = new BookFromAPIDAO(connection);  // Truyền connection vào DAO
    }

    @FXML
    public void initialize() {
        // Chia tỷ lệ các cột
        idColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.05));
        isbnColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.15));
        titleColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.25));
        authorColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.20));
        publisherColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.15));
        publishedDateColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.10));
        statusColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.10));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publishedDateColumn.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load dữ liệu ban đầu từ cơ sở dữ liệu
    }


}
