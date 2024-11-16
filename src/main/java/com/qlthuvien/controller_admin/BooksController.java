package com.qlthuvien.controller_admin;

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

        refreshBooksTable();

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

    @FXML
    public void addBook() {
        try {
            Book newBook = new Book(0, titleInput.getText(), authorInput.getText(), "available", genreInput.getText());
            bookDAO.add(newBook);
            showSuccess("Book added successfully!");
            refreshBooksTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void editBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("No book selected");
            return;
        }

        try {
            selectedBook.setTitle(titleInput.getText());
            selectedBook.setAuthor(authorInput.getText());
            selectedBook.setGenre(genreInput.getText());
            bookDAO.update(selectedBook);
            showSuccess("Book updated successfully!");
            refreshBooksTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void deleteBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("No book selected");
            return;
        }

        try {
            bookDAO.delete(selectedBook.getId());
            showSuccess("Book deleted successfully!");
            refreshBooksTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void generateQR() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("No book selected");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save QR Code");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                String qrContent = "Type: BOOK, ID: " + selectedBook.getId() + ", Title: " + selectedBook.getTitle() +
                        ", Author: " + selectedBook.getAuthor() + ", Genre: " + selectedBook.getGenre();
                QRCodeGenerator.generateQRCode(qrContent, file.getAbsolutePath());
                showSuccess("QR Code generated successfully!");
            } catch (Exception e) {
                showError("Error generating QR code: " + e.getMessage());
            }
        }
    }

    private void refreshBooksTable() {
        statusLabel.setText("Loading...");

        DatabaseTask.run(
                () -> {
                    return bookDAO.getAll();
                },
                books -> {
                    booksTable.getItems().setAll(books);
                    statusLabel.setText("Load complete!");
                },
                exception -> {
                    showError(exception.getMessage());
                    statusLabel.setText("Failed to load data");
                }
        );
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
