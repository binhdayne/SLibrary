package com.qlthuvien.controller_admin;

import java.sql.Connection;
import java.sql.SQLException;

import com.qlthuvien.api.GoogleBooksAPI;
import com.qlthuvien.dao.BookFromAPIDAO;
import com.qlthuvien.model.BookFromAPI;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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
    }
    
    /**
     * Fetches book information from Google Books API using ISBN.
     * Updates the input fields with fetched book details if found.
     * Updates status label to show progress and results of the API call.
     */
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
    
    /**
     * Adds a new book to the database using information from input fields.
     * Creates a new BookFromAPI object and saves it to the database.
     * Refreshes the table view and shows success/error message.
     * Sets default status as "available" for new books.
     */
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
}
