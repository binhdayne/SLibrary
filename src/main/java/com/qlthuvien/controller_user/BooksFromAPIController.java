package com.qlthuvien.controller_user;

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

        refreshBooksTable();
    }

    @FXML
    public void fetchFromAPI() {
        String isbn = isbnInput.getText();
        statusApiLabel.setText("Fetching from API...");

        DatabaseTask.run(
                () -> GoogleBooksAPI.fetchBookByISBN(isbn),
                book -> {
                    if (book != null) {
                        titleInput.setText(book.getTitle());
                        authorInput.setText(book.getAuthor());
                        publisherInput.setText(book.getPublisher());
                        publishedDateInput.setText(book.getPublishedDate());
                        descriptionInput.setText(book.getDescription());
                        statusApiLabel.setText("Book fetched successfully from API.");
                    } else {
                        statusApiLabel.setText("No book found for this ISBN.");
                    }
                },
                exception -> statusApiLabel.setText("Error fetching book from API: " + exception.getMessage())
        );
    }

    @FXML
    public void addBook() {
        try {
            BookFromAPI book = new BookFromAPI(
                    0,
                    titleInput.getText(),
                    authorInput.getText(),
                    "available",  // Set status mặc định là "available"
                    isbnInput.getText(),
                    publisherInput.getText(),
                    publishedDateInput.getText(),
                    descriptionInput.getText()
            );
            bookFromAPIDAO.add(book);
            refreshBooksTable();
            showAlert("Success", "Book added successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Error", "Error adding book: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void deleteBook() {
        BookFromAPI selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            try {
                bookFromAPIDAO.deleteByIsbn(selectedBook.getIsbn());  // Xóa tài liệu từ database bằng ISBN
                refreshBooksTable();
                showAlert("Success", "Book deleted successfully!", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Error deleting book: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Error", "No book selected for deletion.", Alert.AlertType.ERROR);
        }
    }

    private void refreshBooksTable() {
        statusLoadDataLabel.setText("Loading data...");

        DatabaseTask.run(
                () -> bookFromAPIDAO.getAll(),
                books -> {
                    booksTable.getItems().setAll(books);
                    statusLoadDataLabel.setText("Data loaded successfully!");
                },
                exception -> statusLoadDataLabel.setText("Error fetching books: " + exception.getMessage())
        );
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
