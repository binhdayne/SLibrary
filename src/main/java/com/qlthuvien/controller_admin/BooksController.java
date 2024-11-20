package com.qlthuvien.controller_admin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import com.qlthuvien.dao.BookDAO;
import com.qlthuvien.model.Book;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import com.qlthuvien.utils.QRCodeGenerator;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    
    /**
     * Initializes the controller and sets up the TableView.
     * Configures column widths, cell value factories, and selection handling.
     */
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
        // Handle book selection
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
    
    /**
     * Adds a new book to the library system.
     * Creates a new Book object from input fields and saves it to the database.
     * Updates the table view and shows success/error message.
     */

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
    
    /**
     * Updates the selected book's information in the database.
     * Takes values from input fields and updates the corresponding book record.
     * Shows error message if no book is selected.
     */

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

    /**
     * Deletes the selected book from the library system.
     * Removes the book record from the database and updates the table view.
     * Shows error message if no book is selected.
     */
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
    
    /**
     * Generates a QR code for the selected book.
     * QR code contains book details including ID, title, author, and genre.
     * Allows user to choose save location for the generated QR code image.
     * Shows error message if no book is selected or if generation fails.
     */
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

    /**
     * Refreshes the books table with current data from the database.
     * Uses DatabaseTask for asynchronous loading to prevent UI freezing.
     * Updates status label to show loading progress.
     */
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
    
    /**
     * Displays an error message dialog to the user.
     * @param message The error message to be displayed
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
    
    /**
     * Displays a success message dialog to the user.
     * @param message The success message to be displayed
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
