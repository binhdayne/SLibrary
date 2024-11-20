package com.qlthuvien.controller_user;

import com.qlthuvien.dao.BookDAO;
import com.qlthuvien.model.Book;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import com.qlthuvien.utils.QRCodeGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

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
    private Button generateQRButton, borrowBookButton;

    private String userId; // User ID from another FXML (set via label)

    // Setter to receive `userId` from another controller or FXML
    public void setUserId(String userId) {
        this.userId = userId;
    }

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

        // Handle table row selection
        booksTable.setOnMouseClicked(event -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                titleInput.setText(selectedBook.getTitle());
                authorInput.setText(selectedBook.getAuthor());
                genreInput.setText(selectedBook.getGenre());
                borrowBookButton.setDisable(false); // Enable borrow button
                generateQRButton.setDisable(false);
            } else {
                borrowBookButton.setDisable(true); // Disable borrow button
                generateQRButton.setDisable(true);
            }
        });

        // Disable the borrow and QR buttons initially
        borrowBookButton.setDisable(true);
        generateQRButton.setDisable(true);
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
                () -> bookDAO.getAll(),
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

    @FXML
    private void borrowBook() {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("No book selected.");
            return;
        }

        if (userId == null || userId.isEmpty()) {
            showError("Membership ID is missing. Please log in.");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try (Connection connection = DBConnection.getConnection()) {
                // Step 1: Check if the user has already borrowed or is waiting for 3 or more books
                String countQuery = """
                    SELECT 
                        (SELECT COUNT(*) FROM borrow_return WHERE membership_id = ? AND status = 'Borrowed') +
                        (SELECT COUNT(*) FROM waiting_borrow WHERE membership_id = ?) AS total_count
                    """;
                PreparedStatement countStmt = connection.prepareStatement(countQuery);
                countStmt.setString(1, userId);
                countStmt.setString(2, userId);
                ResultSet countResult = countStmt.executeQuery();

                if (countResult.next() && countResult.getInt("total_count") >= 3) {
                    Platform.runLater(() -> showError("You cannot reserve more than 3 documents (Borrowed + Waiting)."));
                    return;
                }

                // Step 2: Check the availability of the selected book
                if (!"available".equalsIgnoreCase(selectedBook.getStatus())) {
                    Platform.runLater(() -> showError("Book is not available for borrowing."));
                    return;
                }

                // Step 3: Mark the book as "Waiting" in the database
                String updateBookStatusQuery = "UPDATE books SET status = 'Waiting' WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateBookStatusQuery);
                updateStmt.setInt(1, selectedBook.getId());
                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    // Step 4: Insert a record into the waiting_borrow table
                    String insertBorrowQuery = """
                        INSERT INTO waiting_borrow (membership_id, document_id, document_type, borrow_date, status)
                        VALUES (?, ?, 'BOOK', NOW(), 'Waiting')
                        """;
                    PreparedStatement insertStmt = connection.prepareStatement(insertBorrowQuery);
                    insertStmt.setString(1, userId);
                    insertStmt.setInt(2, selectedBook.getId());
                    insertStmt.executeUpdate();

                    // Update the UI
                    Platform.runLater(() -> {
                        selectedBook.setStatus("Waiting");
                        booksTable.refresh();
                        showSuccess("Book has been reserved successfully!");
                    });
                } else {
                    Platform.runLater(() -> showError("Failed to update book status."));
                }
            } catch (SQLException e) {
                Platform.runLater(() -> showError("Database error: " + e.getMessage()));
            }
        });
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
