package com.qlthuvien.controller;

import com.qlthuvien.dao.BookDAO;
import com.qlthuvien.model.Book;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import com.qlthuvien.utils.QRCodeGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BooksController {

    private Connection connection;
    private BookDAO bookDAO;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, Integer> idColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> genreColumn;
    @FXML
    private TableColumn<Book, String> statusColumn;

    @FXML
    private TextField titleInput, authorInput, genreInput;

    @FXML
    private Label statusLabel;

    @FXML
    private Button generateQRButton;

    /**
     * Constructor BooksController.
     * Khởi tạo kết nối với cơ sở dữ liệu và đối tượng BookDAO.
     */
    public BooksController() {
        connection = DBConnection.getConnection();
        bookDAO = new BookDAO(connection);
    }

    @FXML
    public void initialize() {
        idColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.10));
        titleColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.30));
        authorColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.25));
        genreColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.25));
        statusColumn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.10));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load dữ liệu ban đầu từ cơ sở dữ liệu

        booksTable.setOnMouseClicked(event -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                titleInput.setText(selectedBook.getTitle());
                authorInput.setText(selectedBook.getAuthor());
                genreInput.setText(selectedBook.getGenre());
                generateQRButton.setDisable(false);
            } else {
                generateQRButton.setDisable(true);
            }
        });
    }


}
