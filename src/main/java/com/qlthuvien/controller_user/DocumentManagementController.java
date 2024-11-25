package com.qlthuvien.controller_user;

import com.qlthuvien.api.GoogleBooksAPI;
import com.qlthuvien.dao.*;
import com.qlthuvien.model.*;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import com.qlthuvien.utils.QRCodeGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DocumentManagementController extends BaseController {

    private Connection connection;
    private BookDAO bookDAO;
    private MagazineDAO magazineDAO;
    private ThesisDAO thesisDAO;
    private BookFromAPIDAO bookFromAPIDAO;

    @FXML
    private TableView<BookFromAPI> bookfromAPITable;
    // TableViews for different document types
    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableView<Magazine> magazinesTable;
    @FXML
    private TableView<Thesis> thesesTable;
    @FXML
    private TableView<BookFromAPI> booksFromAPITable;
    @FXML
    private TableColumn<Book, Integer> bookIdColumn;
    @FXML
    private TableColumn<Book, String> bookTitleColumn, bookAuthorColumn, bookGenreColumn, bookStatusColumn;
    @FXML
    private TableColumn<Magazine, Integer> magazineIdColumn, issueNumberColumn;
    @FXML
    private TableColumn<Magazine, String> magazineTitleColumn, magazineAuthorColumn, magazinePublisherColumn, magazineStatusColumn;
    @FXML
    private TableColumn<Thesis, Integer> thesisIdColumn;
    @FXML
    private TableColumn<Thesis, String> thesisTitleColumn, thesisAuthorColumn, thesisSupervisorColumn, thesisUniversityColumn, thesisStatusColumn;
    @FXML
    private TableColumn<BookFromAPI, Integer> apiBookIdColumn;
    @FXML
    private TableColumn<BookFromAPI, String> apiBookIsbnColumn, apiBookTitleColumn, apiBookAuthorColumn, apiBookPublisherColumn, apiBookDescriptionColumn, apiBookStatusColumn;
    @FXML
    private TabPane documentTabPane;
    @FXML
    private Tab bookTab, magazineTab, thesisTab, booksFromAPITab;
    @FXML
    private TextField titleInput, authorInput, statusInput;
    @FXML
    private Label statusLabel, nameLabel;
    @FXML
    private TextField searchInput;
    @FXML
    private Button searchButton, borrowBookButton;
    @FXML
    private ImageView documentCoverImageView;

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
        // Setup the tables
        setupBooksTable();
        setupMagazinesTable();
        setupThesesTable();
        setupBooksFromAPITable();

        refreshAllTables();

        searchInput.textProperty().addListener((observable, oldValue, newValue) -> searchAllDocuments(newValue));
        searchButton.setOnAction(event -> searchAllDocuments(searchInput.getText()));
        borrowBookButton.setDisable(true);
    }

    private void displayCover(String coverPath) {
        if (coverPath != null && !coverPath.isEmpty()) {
            File coverFile = new File(coverPath);
            if (coverFile.exists()) {
                documentCoverImageView.setImage(new javafx.scene.image.Image(coverFile.toURI().toString()));
            } else {
                documentCoverImageView.setImage(null); // Clear the view if the file does not exist
            }
        } else {
            documentCoverImageView.setImage(null); // Clear the view if no cover path is set
        }
    }


    private void searchAllDocuments(String query) {
        if (query == null || query.isEmpty()) {
            refreshAllTables(); // Reload all data if the search query is empty
            return;
        }

        Tab selectedTab = documentTabPane.getSelectionModel().getSelectedItem();

        if (selectedTab == bookTab) {
            // Search in books table
            CompletableFuture.runAsync(() -> {
                try {
                    List<Book> filteredBooks = bookDAO.searchBooksByTitleOrAuthor(query);
                    Platform.runLater(() -> booksTable.getItems().setAll(filteredBooks));
                } catch (SQLException e) {
                    Platform.runLater(() -> showError("Error searching books: " + e.getMessage()));
                }
            });
        } else if (selectedTab == magazineTab) {
            // Search in magazines table
            CompletableFuture.runAsync(() -> {
                try {
                    List<Magazine> filteredMagazines = magazineDAO.searchMagazinesByTitleOrAuthor(query);
                    Platform.runLater(() -> magazinesTable.getItems().setAll(filteredMagazines));
                } catch (SQLException e) {
                    Platform.runLater(() -> showError("Error searching magazines: " + e.getMessage()));
                }
            });
        } else if (selectedTab == thesisTab) {
            CompletableFuture.runAsync(() -> {
                try {
                    List<Thesis> filteredTheses = thesisDAO.searchThesesByTitleOrAuthor(query);
                    Platform.runLater(() -> thesesTable.getItems().setAll(filteredTheses));
                } catch (SQLException e) {
                    Platform.runLater(() -> showError("Error searching theses: " + e.getMessage()));
                }
            });
        } else if (selectedTab == booksFromAPITab) {
            // Search in books from API table
            CompletableFuture.runAsync(() -> {
                try {
                    List<BookFromAPI> filteredBooksFromAPI = bookFromAPIDAO.searchBooksAPIByTitleOrAuthor(query);
                    Platform.runLater(() -> booksFromAPITable.getItems().setAll(filteredBooksFromAPI));
                } catch (SQLException e) {
                    Platform.runLater(() -> showError("Error searching books from API: " + e.getMessage()));
                }
            });
        }
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
                titleInput.setText(selectedBook.getTitle());
                authorInput.setText(selectedBook.getAuthor());
                statusInput.setText(selectedBook.getStatus());
                displayCover(selectedBook.getBookcover()); // Display cover from the database
                borrowBookButton.setDisable(false);
            } else {
                clearInputsAndCover();
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
                displayCover(selectedMagazine.getCoverPath()); // Display cover from the database
                borrowBookButton.setDisable(false);
            } else {
                clearInputsAndCover();
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
                displayCover(selectedThesis.getCoverPath()); // Display cover from the database
                borrowBookButton.setDisable(false);
            } else {
                clearInputsAndCover();
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
                fetchAndDisplayBookCover(selectedAPIBook.getIsbn());
                borrowBookButton.setDisable(false);
            } else {
                clearInputsAndCover();
            }
        });
    }

    private void fetchAndDisplayBookCover(String isbn) {
        DatabaseTask.run(
                () -> GoogleBooksAPI.fetchBookByISBN(isbn),
                book -> {
                    if (book != null && book.getBookCover() != null) {
                        Platform.runLater(() -> displayCover(book.getBookCover()));
                    } else {
                        Platform.runLater(() -> documentCoverImageView.setImage(null));
                    }
                },
                exception -> {
                    System.err.println("Error fetching book cover: " + exception.getMessage());
                    Platform.runLater(() -> documentCoverImageView.setImage(null));
                }
        );
    }

    private void clearInputsAndCover() {
        titleInput.clear();
        authorInput.clear();
        statusInput.clear();
        documentCoverImageView.setImage(null);
        borrowBookButton.setDisable(true);
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

        // Kiểm tra nếu sách ko được chọn
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



//    private void showError(String message) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.setContentText(message);
//        alert.show();
//    }

//    private void showSuccess(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setContentText(message);
//        alert.show();
//    }

//    private void showInfo(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Information");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }

//    private void showAlert(String title, String message, Alert.AlertType alertType) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
}