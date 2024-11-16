package com.qlthuvien.controller_user;

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
import java.util.concurrent.CompletableFuture;

public class HomeTabUser {

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
    private Button addBookButton;

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

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }

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

                    // Tạo một mục sách và gán sự kiện click để hiển thị chi tiết
                    addBookToDisplay(title, authors, description, thumbnailUrl);
                }
            }
        }
    }

    private void addBookToDisplay(String title, String authors, String description, String thumbnailUrl) {
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
        bookItem.setOnMouseClicked(event -> displayBookDetails(title, authors, description, thumbnailUrl));

        bestsellingBooksList.getChildren().add(bookItem);
    }

    private void displayBookDetails(String title, String authors, String description, String thumbnailUrl) {
        bookTitleLabel.setText(title);
        bookAuthorLabel.setText(authors);
        bookDescriptionLabel.setText(description);
        bookThumbnail.setImage(!thumbnailUrl.isEmpty() ? new Image(thumbnailUrl, true) : null);
        selectedBookDetails.setVisible(true);
    }

    @FXML
    private void addBook() {
        // Thêm logic cho hành động khi nhấn nút Add Book
        showInfo("Book added successfully!");
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
