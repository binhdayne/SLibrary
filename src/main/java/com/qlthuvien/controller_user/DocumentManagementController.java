package com.qlthuvien.controller_user;

import com.qlthuvien.dao.*;
import com.qlthuvien.model.*;
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

public class DocumentManagementController {

    private Connection connection;
    private BookDAO bookDAO;
    private MagazineDAO magazineDAO;
    private ThesisDAO thesisDAO;
    private BookFromAPIDAO bookFromAPIDAO;

    // TableViews for different document types
    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableView<Magazine> magazinesTable;
    @FXML
    private TableView<Thesis> thesesTable;
    @FXML
    private TableView<BookFromAPI> booksFromAPITable;

    // Columns for books table
    @FXML
    private TableColumn<Book, Integer> bookIdColumn;
    @FXML
    private TableColumn<Book, String> bookTitleColumn, bookAuthorColumn, bookGenreColumn, bookStatusColumn;

    // Columns for magazines table
    @FXML
    private TableColumn<Magazine, Integer> magazineIdColumn, issueNumberColumn;
    @FXML
    private TableColumn<Magazine, String> magazineTitleColumn, magazineAuthorColumn, magazinePublisherColumn, magazineStatusColumn;

    // Columns for theses table
    @FXML
    private TableColumn<Thesis, Integer> thesisIdColumn;
    @FXML
    private TableColumn<Thesis, String> thesisTitleColumn, thesisAuthorColumn, thesisSupervisorColumn, thesisUniversityColumn, thesisStatusColumn;

    // Columns for books_from_api table
    @FXML
    private TableColumn<BookFromAPI, Integer> apiBookIdColumn;
    @FXML
    private TableColumn<BookFromAPI, String> apiBookIsbnColumn, apiBookTitleColumn, apiBookAuthorColumn, apiBookPublisherColumn, apiBookDescriptionColumn, apiBookStatusColumn;

    @FXML
    private TextField titleInput, authorInput, statusInput;

    @FXML
    private Label statusLabel;

    @FXML
    private Button generateQRButton, borrowBookButton;
    @FXML
    private Label nameLabel;

    private String userId; // User ID from another FXML (set via label)

    public DocumentManagementController() {
        connection = DBConnection.getConnection();
        bookDAO = new BookDAO(connection);
        magazineDAO = new MagazineDAO(connection);
        thesisDAO = new ThesisDAO(connection);
        bookFromAPIDAO = new BookFromAPIDAO(connection);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @FXML
    public void initialize() {
        setupBooksTable();
        setupMagazinesTable();
        setupThesesTable();
        setupBooksFromAPITable();

        refreshAllTables();

        generateQRButton.setDisable(true);
        borrowBookButton.setDisable(true);
    }

    private void setupBooksTable() {
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookGenreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        bookStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        booksTable.setOnMouseClicked(event -> {
            Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                // Hiển thị thông tin sách trong các TextField
                titleInput.setText(selectedBook.getTitle());
                authorInput.setText(selectedBook.getAuthor());
                statusInput.setText(selectedBook.getStatus());

                borrowBookButton.setDisable(false);
                generateQRButton.setDisable(false);
            } else {
                // Xóa thông tin khi không chọn sách nào
                titleInput.clear();
                authorInput.clear();
                statusInput.clear();

                borrowBookButton.setDisable(true);
                generateQRButton.setDisable(true);
            }
        });
    }
    private void setupMagazinesTable() {
        magazineIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        magazineTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        magazineAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        magazinePublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        magazineStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        issueNumberColumn.setCellValueFactory(new PropertyValueFactory<>("issueNumber"));

        magazinesTable.setOnMouseClicked(event -> {
            Magazine selectedMagazine = magazinesTable.getSelectionModel().getSelectedItem();
            if (selectedMagazine != null) {
                titleInput.setText(selectedMagazine.getTitle());
                authorInput.setText(selectedMagazine.getAuthor());
                statusInput.setText(selectedMagazine.getStatus());

                borrowBookButton.setDisable(false);
                generateQRButton.setDisable(false);
            } else {
                titleInput.clear();
                authorInput.clear();
                statusInput.clear();

                borrowBookButton.setDisable(true);
                generateQRButton.setDisable(true);
            }
        });
    }


    private void setupThesesTable() {
        thesisIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        thesisTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        thesisAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        thesisSupervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisor"));
        thesisUniversityColumn.setCellValueFactory(new PropertyValueFactory<>("university"));
        thesisStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        thesesTable.setOnMouseClicked(event -> {
            Thesis selectedThesis = thesesTable.getSelectionModel().getSelectedItem();
            if (selectedThesis != null) {
                titleInput.setText(selectedThesis.getTitle());
                authorInput.setText(selectedThesis.getAuthor());
                statusInput.setText(selectedThesis.getStatus());

                borrowBookButton.setDisable(false);
                generateQRButton.setDisable(false);
            } else {
                titleInput.clear();
                authorInput.clear();
                statusInput.clear();

                borrowBookButton.setDisable(true);
                generateQRButton.setDisable(true);
            }
        });
    }


    private void setupBooksFromAPITable() {
        apiBookIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        apiBookIsbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        apiBookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        apiBookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        apiBookPublisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        apiBookDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        apiBookStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        booksFromAPITable.setOnMouseClicked(event -> {
            BookFromAPI selectedAPIBook = booksFromAPITable.getSelectionModel().getSelectedItem();
            if (selectedAPIBook != null) {
                titleInput.setText(selectedAPIBook.getTitle());
                authorInput.setText(selectedAPIBook.getAuthor());
                statusInput.setText(selectedAPIBook.getStatus());
                borrowBookButton.setDisable(false);
                generateQRButton.setDisable(false);
            } else {
                titleInput.clear();
                authorInput.clear();
                statusInput.clear();
                borrowBookButton.setDisable(true);
                generateQRButton.setDisable(true);
            }
        });
    }


    private void refreshAllTables() {
        statusLabel.setText("Loading...");
        DatabaseTask.run(
                () -> {
                    booksTable.getItems().setAll(bookDAO.getAll());
                    magazinesTable.getItems().setAll(magazineDAO.getAll());
                    thesesTable.getItems().setAll(thesisDAO.getAll());
                    booksFromAPITable.getItems().setAll(bookFromAPIDAO.getAll());
                    return null;
                },
                result -> statusLabel.setText("Load complete!"),
                exception -> {
                    showError(exception.getMessage());
                    statusLabel.setText("Failed to load data");
                }
        );
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

    private Object getSelectedDocument() {
        if (booksTable.getSelectionModel().getSelectedItem() != null) {
            return booksTable.getSelectionModel().getSelectedItem();
        } else if (magazinesTable.getSelectionModel().getSelectedItem() != null) {
            return magazinesTable.getSelectionModel().getSelectedItem();
        } else if (thesesTable.getSelectionModel().getSelectedItem() != null) {
            return thesesTable.getSelectionModel().getSelectedItem();
        } else if (booksFromAPITable.getSelectionModel().getSelectedItem() != null) {
            return booksFromAPITable.getSelectionModel().getSelectedItem();
        }
        return null;
    }

    private String getTableName(String documentType) {
        return switch (documentType) {
            case "BOOK" -> "books";
            case "MAGAZINE" -> "magazines";
            case "THESIS" -> "theses";
            case "BOOK_FROM_API" -> "books_from_api";
            default -> throw new IllegalArgumentException("Unknown document type: " + documentType);
        };
    }

    private void setStatus(Object document, String status) {
        if (document instanceof Book book) {
            book.setStatus(status);
        } else if (document instanceof Magazine magazine) {
            magazine.setStatus(status);
        } else if (document instanceof Thesis thesis) {
            thesis.setStatus(status);
        } else if (document instanceof BookFromAPI bookFromAPI) {
            bookFromAPI.setStatus(status);
        }
    }

    private void refreshTable(String documentType) {
        switch (documentType) {
            case "BOOK" -> booksTable.refresh();
            case "MAGAZINE" -> magazinesTable.refresh();
            case "THESIS" -> thesesTable.refresh();
            case "BOOK_FROM_API" -> booksFromAPITable.refresh();
            default -> throw new IllegalArgumentException("Unknown document type: " + documentType);
        }
    }


    @FXML
    private void borrowBook() {
        // Kiểm tra tài liệu được chọn từ bảng nào
        Object selectedDocument = getSelectedDocument();

        // Check if no document is selected
        if (selectedDocument == null) {
            showError("No document selected.");
            return;
        }

        // Check if the user is logged in
        if (userId == null || userId.isEmpty()) {
            showError("Membership ID is missing. Please log in.");
            return;
        }

        // Run database operations asynchronously
        CompletableFuture.runAsync(() -> {
            try (Connection connection = DBConnection.getConnection()) {
                // Step 1: Check number of documents in waiting_borrow
                String countWaitingQuery = """  
            SELECT COUNT(*) AS waiting_count  
            FROM waiting_borrow  
            WHERE membership_id = ?  
            """;
                try (PreparedStatement countWaitingStmt = connection.prepareStatement(countWaitingQuery)) {
                    countWaitingStmt.setString(1, userId);
                    try (ResultSet countWaitingResult = countWaitingStmt.executeQuery()) {
                        if (countWaitingResult.next() && countWaitingResult.getInt("waiting_count") >= 3) {
                            Platform.runLater(() -> showError("You cannot borrow more than 3 documents."));
                            return;
                        }
                    }
                }

                // Step 2: Determine document type and status
                String status = "";
                int documentId = -1;
                String documentType;

                if (selectedDocument instanceof Book book) {
                    status = book.getStatus();
                    documentId = book.getId();
                    documentType = "BOOK";
                } else if (selectedDocument instanceof Magazine magazine) {
                    status = magazine.getStatus();
                    documentId = magazine.getId();
                    documentType = "MAGAZINE";
                } else if (selectedDocument instanceof Thesis thesis) {
                    status = thesis.getStatus();
                    documentId = thesis.getId();
                    documentType = "THESIS";
                } else if (selectedDocument instanceof BookFromAPI booksFromApi) {
                    status = booksFromApi.getStatus();
                    documentId = booksFromApi.getId();
                    documentType = "BOOK_FROM_API";
                } else {
                    documentType = null;
                }

                if (documentType == null) {
                    Platform.runLater(() -> showError("Unknown document type."));
                    return;
                }

                // Check if the document is available for borrowing
                if (!"available".equalsIgnoreCase(status)) {
                    Platform.runLater(() -> showError("Document is not available for borrowing."));
                    return;
                }

                // Step 3: Mark the document as "Waiting" in the database
                String updateStatusQuery = "UPDATE " + getTableName(documentType) + " SET status = 'Waiting' WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateStatusQuery)) {
                    updateStmt.setInt(1, documentId);
                    int rowsUpdated = updateStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        // Step 4: Insert a record into waiting_borrow table
                        String insertBorrowQuery = """  
                    INSERT INTO waiting_borrow (membership_id, document_id, document_type, borrow_date, status)  
                    VALUES (?, ?, ?, NOW(), 'Waiting')  
                    """;
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertBorrowQuery)) {
                            insertStmt.setString(1, userId);
                            insertStmt.setInt(2, documentId);
                            insertStmt.setString(3, documentType);
                            insertStmt.executeUpdate();
                        }

                        // Update UI to reflect the reservation
                        Platform.runLater(() -> {
                            setStatus(selectedDocument, "Waiting");
                            refreshTable(documentType);
                            showSuccess("Document has been reserved successfully!");
                        });
                    } else {
                        Platform.runLater(() -> showError("Failed to update document status."));
                    }
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