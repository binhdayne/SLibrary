package com.qlthuvien.controller_admin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import com.google.zxing.Result;
import com.qlthuvien.api.GoogleBooksAPI;
import com.qlthuvien.dao.BookFromAPIDAO;
import com.qlthuvien.model.BookFromAPI;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import com.qlthuvien.utils.QRCodeGenerator;

import com.qlthuvien.utils.StarAnimationUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    @FXML
    private Button generateQRButton;
    @FXML
    private ImageView bookCoverImageView;
    @FXML
    private VBox starContainer;
    private BookFromAPIDAO bookFromAPIDAO;

    /**
     * Constructor for BooksFromAPIController.
     * Initializes database connection and BookFromAPIDAO instance.
     */
    public BooksFromAPIController() {
        Connection connection = DBConnection.getConnection();  // Get connection from DBConnection
        this.bookFromAPIDAO = new BookFromAPIDAO(connection);  // Pass connection to DAO
    }

    /**
     * Initializes the controller and sets up the TableView columns.
     * Configures column widths as percentages of table width and sets up cell value factories.
     * Loads initial book data into the table.
     */
    @FXML
    public void initialize() {
        // Set column widths based on table size
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

        // Handle row selection
        booksTable.setOnMouseClicked(event -> {
            BookFromAPI selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                populateBookDetails(selectedBook); // Populate fields with selected book details
                fetchAndDisplayBookCover(selectedBook.getIsbn()); // Fetch and display the book cover
                generateQRButton.setDisable(false);
            } else {
                clearBookDetails();
                bookCoverImageView.setImage(null);
                generateQRButton.setDisable(true);
            }
        });

        // Create star animation
        if (starContainer != null) {
            Platform.runLater(() -> {
                StarAnimationUtil.createStarAnimation(starContainer);
            });
        }
    }

    private void populateBookDetails(BookFromAPI book) {
        isbnInput.setText(book.getIsbn());
        titleInput.setText(book.getTitle());
        authorInput.setText(book.getAuthor());
        publisherInput.setText(book.getPublisher());
        publishedDateInput.setText(book.getPublishedDate());
        descriptionInput.setText(book.getDescription());
    }

    private void clearBookDetails() {
        isbnInput.clear();
        titleInput.clear();
        authorInput.clear();
        publisherInput.clear();
        publishedDateInput.clear();
        descriptionInput.clear();
    }

    @FXML
    public void fetchFromAPI() {
        String isbn = isbnInput.getText();
        statusApiLabel.setText("Fetching from API...");

        DatabaseTask.run(
                () -> GoogleBooksAPI.fetchBookByISBN(isbn), // Fetch book details from API
                book -> {
                    if (book != null) {
                        // Populate the input fields with book details
                        titleInput.setText(book.getTitle());
                        authorInput.setText(book.getAuthor());
                        publisherInput.setText(book.getPublisher());
                        publishedDateInput.setText(book.getPublishedDate());
                        descriptionInput.setText(book.getDescription());

                        // Display the book cover in the ImageView
                        displayBookCover(book.getBookCover());
                        statusApiLabel.setText("Book fetched successfully from API.");
                    } else {
                        statusApiLabel.setText("No book found for this ISBN.");
                        bookCoverImageView.setImage(null); // Clear the cover view
                    }
                },
                exception -> {
                    statusApiLabel.setText("Error fetching book from API: " + exception.getMessage());
                    bookCoverImageView.setImage(null); // Clear the cover view on error
                }
        );
    }

    private void fetchAndDisplayBookCover(String isbn) {
        DatabaseTask.run(
                () -> GoogleBooksAPI.fetchBookByISBN(isbn), // Fetch book details from API
                book -> {
                    if (book != null && book.getBookCover() != null) {
                        displayBookCover(book.getBookCover());
                    } else {
                        bookCoverImageView.setImage(null); // Clear the ImageView if no thumbnail is found
                    }
                },
                exception -> {
                    System.err.println("Error fetching book cover: " + exception.getMessage());
                    bookCoverImageView.setImage(null); // Clear the ImageView on error
                }
        );
    }

    private void displayBookCover(String thumbnailUrl) {
        try {
            Image bookCoverImage = new Image(thumbnailUrl);
            bookCoverImageView.setImage(bookCoverImage); // Set the image in the ImageView
        } catch (Exception e) {
            System.err.println("Error loading book cover: " + e.getMessage());
            bookCoverImageView.setImage(null);
        }
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

    /**
     * Deletes the selected book from the database.
     * Removes book record using ISBN as the identifier.
     * Shows error message if no book is selected.
     * Refreshes table view after successful deletion.
     */
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

    /**
     * Refreshes the books table with current data from the database.
     * Uses DatabaseTask for asynchronous loading to prevent UI freezing.
     * Updates status label to show loading progress and results.
     */
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

    /**
     * Displays an alert dialog to the user.
     * @param title The title of the alert dialog
     * @param message The message to be displayed in the alert
     * @param alertType The type of alert (ERROR, INFORMATION, etc.)
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Generates QR code for the selected book.
     * Shows a file chooser dialog to save the QR code as a PNG file.
     * Uses QRCodeGenerator to generate the QR code and save it to the specified file.
     * Shows success/error message based on the result of the QR code generation.
     */
    @FXML
    public void generateQR() {
        BookFromAPI selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert("Error", "No book selected", Alert.AlertType.ERROR);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save QR Code");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                String qrContent = "Type: API_BOOK, ID: " + selectedBook.getId() +
                        ", ISBN: " + selectedBook.getIsbn() +
                        ", Title: " + selectedBook.getTitle() +
                        ", Author: " + selectedBook.getAuthor() +
                        ", Publisher: " + selectedBook.getPublisher() +
                        ", Published Date: " + selectedBook.getPublishedDate();
                QRCodeGenerator.generateQRCode(qrContent, file.getAbsolutePath());
                showAlert("Success", "QR Code generated successfully!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "Error generating QR code: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}