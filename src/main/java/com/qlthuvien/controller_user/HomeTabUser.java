package com.qlthuvien.controller_user;

import com.qlthuvien.dao.BorrowReturnDAO;
import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.utils.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javafx.application.Platform;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class HomeTabUser extends BaseController{

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private VBox bestsellingBooksList;
    @FXML
    private VBox selectedBookDetails;
    @FXML
    private Label bookTitleLabel;
    @FXML
    private Label bookAuthorLabel;
    @FXML
    private ImageView bookThumbnail;
    @FXML
    private Label bookDescriptionLabel;
    @FXML
    private Label bookISBNLabel; // Thêm Label để hiển thị ISBN
    @FXML
    private Button borrowBookButton;

    private String userId;

    // Setter để nhận `userId` từ `MainUserController`
    public void setUserId(String userId) {
        this.userId = userId;
    }


//    private static final String DB_URL = "jdbc:mysql://localhost:3306/slibrary"; // Thay bằng URL của bạn
//    private static final String DB_USER = "root"; // Tên tài khoản MySQL
//    private static final String DB_PASSWORD = "14112005"; // Mật khẩu MySQL
//
//    private Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//    }
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    @FXML
    public void initialize() {
        searchButton.setOnAction(event -> searchBooks());
        selectedBookDetails.setVisible(false);  // Ẩn phần chi tiết sách khi khởi động
    }

    @FXML
    public void searchBooks() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            showError("Please enter a search query.");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                String jsonResponse = fetchBooksData(query);
                Platform.runLater(() -> {
                    bestsellingBooksList.getChildren().clear();
                    parseAndDisplayBooks(jsonResponse);
                });
            } catch (IOException e) {
                showError("Failed to fetch book data. Check your internet connection.");
            } catch (Exception e) {
                showError("An error occurred: " + e.getMessage());
            }
        });
    }

//    private void showError(String message) {
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText(message);
//            alert.show();
//        });
//    }

    private String fetchBooksData(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        URL url = new URL(GOOGLE_BOOKS_API_URL + encodedQuery);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode == 429) {
            throw new IOException("Rate limit exceeded. Please try again later.");
        } else if (responseCode != 200) {
            throw new IOException("Error response from server: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        return content.toString();
    }

    private void parseAndDisplayBooks(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray items = jsonObject.optJSONArray("items");

        if (items != null) {
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                JSONObject volumeInfo = item.optJSONObject("volumeInfo");

                if (volumeInfo != null) {
                    String title = volumeInfo.optString("title", "Unknown Title");
                    String authors = volumeInfo.optJSONArray("authors") != null ?
                            volumeInfo.optJSONArray("authors").join(", ").replace("\"", "") : "Unknown Author";
                    String description = volumeInfo.optString("description", "No description available.");
                    String thumbnailUrl = volumeInfo.optJSONObject("imageLinks") != null ?
                            volumeInfo.optJSONObject("imageLinks").optString("thumbnail") : "";
                    String isbn = "Not available";

                    // Lấy ISBN từ JSON
                    JSONArray industryIdentifiers = volumeInfo.optJSONArray("industryIdentifiers");
                    if (industryIdentifiers != null) {
                        for (int j = 0; j < industryIdentifiers.length(); j++) {
                            JSONObject identifier = industryIdentifiers.getJSONObject(j);
                            if ("ISBN_13".equals(identifier.optString("type"))) {
                                isbn = identifier.optString("identifier");
                                break;
                            }
                        }
                    }

                    // Tạo một mục sách và gán sự kiện click để hiển thị chi tiết
                    addBookToDisplay(title, authors, description, thumbnailUrl, isbn);
                }
            }
        }
    }

    private void addBookToDisplay(String title, String authors, String description, String thumbnailUrl, String isbn) {
        HBox bookItem = new HBox(10);
        bookItem.setStyle("-fx-padding: 10; -fx-alignment: CENTER_LEFT;");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(75);
        imageView.setFitWidth(50);
        if (!thumbnailUrl.isEmpty()) {
            imageView.setImage(new Image(thumbnailUrl, true));
        }

        VBox bookInfo = new VBox();
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("book-title");
        Label authorLabel = new Label(authors);
        authorLabel.getStyleClass().add("book-author");

        bookInfo.getChildren().addAll(titleLabel, authorLabel);
        bookItem.getChildren().addAll(imageView, bookInfo);

        // Thêm sự kiện click để hiển thị chi tiết sách
        bookItem.setOnMouseClicked(event -> displayBookDetails(title, authors, description, thumbnailUrl, isbn));

        bestsellingBooksList.getChildren().add(bookItem);
    }

    private void displayBookDetails(String title, String authors, String description, String thumbnailUrl, String isbn) {
        bookTitleLabel.setText(title);
        bookAuthorLabel.setText(authors);
        bookDescriptionLabel.setText(description);
        bookISBNLabel.setText(isbn); // Hiển thị ISBN
        bookThumbnail.setImage(!thumbnailUrl.isEmpty() ? new Image(thumbnailUrl, true) : null);
        selectedBookDetails.setVisible(true);
    }

    @FXML
    private void borrowBook() {
        DBConnection dbConnection = new DBConnection();
        String isbn = bookISBNLabel.getText().replace("ISBN: ", "").trim(); // Lấy ISBN từ bookISBNLabel
        if (isbn.isEmpty()) {
            showError("No book selected or ISBN not available.");
            return;
        }

        if (userId == null || userId.isEmpty()) { // Sử dụng biến userId
            showError("Membership ID is missing. Please log in.");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try (Connection connection = dbConnection.getConnection()) {
                // Kiểm tra tổng số lượng tài liệu "Borrowed" hoặc "Waiting" của người dùng
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

                // Kiểm tra xem sách có tồn tại trong bảng books_from_api không
                String queryCheck = "SELECT id, status FROM books_from_api WHERE isbn = ?";
                PreparedStatement checkStmt = connection.prepareStatement(queryCheck);
                checkStmt.setString(1, isbn);
                ResultSet resultSet = checkStmt.executeQuery();

                if (resultSet.next()) {
                    int bookId = resultSet.getInt("id");
                    String status = resultSet.getString("status");

                    if ("available".equalsIgnoreCase(status)) {
                        // Cập nhật trạng thái sách sang 'Waiting' trong bảng books_from_api
                        String updateBookStatusQuery = "UPDATE books_from_api SET status = 'Waiting' WHERE id = ?";
                        PreparedStatement updateStmt = connection.prepareStatement(updateBookStatusQuery);
                        updateStmt.setInt(1, bookId);
                        updateStmt.executeUpdate();

                        String insertBorrowQuery = "INSERT INTO waiting_borrow (membership_id, document_id, document_type, borrow_date, status) " +
                                "VALUES (?, ?, 'BOOK_FROM_API', NOW(), 'Waiting')";
                        PreparedStatement insertStmt = connection.prepareStatement(insertBorrowQuery);
                        insertStmt.setString(1, userId);
                        insertStmt.setInt(2, bookId);
                        insertStmt.executeUpdate();

                        Platform.runLater(() -> showInfo("Book has been reserved successfully."));
                    } else {
                        Platform.runLater(() -> showError("Book is not available for borrowing."));
                    }
                } else {
                    Platform.runLater(() -> showError("Book with the given ISBN not found in the library."));
                }
            } catch (SQLException e) {
                Platform.runLater(() -> showError("Database error: " + e.getMessage()));
            }
        });
    }

//    private void showInfo(String message) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Information");
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
}
