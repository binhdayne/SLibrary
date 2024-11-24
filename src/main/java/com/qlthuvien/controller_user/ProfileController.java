package com.qlthuvien.controller_user;

import com.qlthuvien.dao.BorrowReturnDAO;
import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.dao.WaitingBorrowDAO;
import com.qlthuvien.model.*;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.QRCodeGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.qlthuvien.dao.BorrowReturnDAO;
import com.qlthuvien.model.BorrowReturn;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.time.format.TextStyle;
import java.util.Locale;
public class ProfileController {

    @FXML
    private TableView<BorrowReturn> borrowReturnTable;
    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableView<Magazine> magazinesTable;
    @FXML
    private TableView<Thesis> thesesTable;
    @FXML
    private TableView<BookFromAPI> booksFromAPITable;
    @FXML
    private TableColumn<BorrowReturn, String> membershipIdColumn;
    @FXML
    private TableColumn<BorrowReturn, String> documentIdColumn;
    @FXML
    private TableColumn<BorrowReturn, String> documentTypeColumn;
    @FXML
    private TableColumn<BorrowReturn, LocalDate> borrowDateColumn;
    @FXML
    private TableColumn<BorrowReturn, LocalDate> returnDateColumn;
    @FXML
    private TableColumn<BorrowReturn, String> statusColumn;
    @FXML
    private GridPane contributionCalendar;
    @FXML
    private TextField nameField, emailField, phoneField;
    @FXML
    private Button editButton, saveButton;

    @FXML
    private Label totalBorrowedLabel, totalReturnedLabel, pendingReturnsLabel, nearestDueDateLabel;

    @FXML
    private TableView<WaitingBorrow> waitingBorrowTable;
    @FXML
    private TableColumn<WaitingBorrow, String> waitingMembershipIdColumn;
    @FXML
    private TableColumn<WaitingBorrow, Integer> waitingDocumentIdColumn;
    @FXML
    private TableColumn<WaitingBorrow, String> waitingDocumentTypeColumn;
    @FXML
    private TableColumn<WaitingBorrow, String> waitingStatusColumn;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private Button deleteWaitingButton, generateQRButton, saveAvatarButton, chooseAvatarButton;



    @FXML
    private ImageView rankImageView;
    // Hàm cập nhật thông tin Dashboard

    private Connection connection;
    private BorrowReturnDAO borrowReturnDAO;
    private WaitingBorrowDAO waitingBorrowDAO;
    private UserDAO userDAO;
    private String userId;
    private String avatarPath;
    private static final int DAYS_IN_WEEK = 7;
    private static final int WEEKS_IN_YEAR = 53;
    private Map<LocalDate, Integer> borrowActivityMap = new HashMap<>();

    public ProfileController() {
        connection = DBConnection.getConnection();
        borrowReturnDAO = new BorrowReturnDAO(connection);
        userDAO = new UserDAO(connection);
        waitingBorrowDAO = new WaitingBorrowDAO(connection);
    }

    public void setUserId(String userId) {
        this.userId = userId;
        loadUserInfo(userId);
        loadUserAvatar(userId);

        List<BorrowReturn> transactions = new ArrayList<>(); // Khởi tạo danh sách trống
        try {
            // Truy vấn các giao dịch, có thể ném ra SQLException
            transactions = borrowReturnDAO.getTransactionsByUser(userId);
        } catch (SQLException e) {
            // Xử lý ngoại lệ: ghi log hoặc hiển thị thông báo lỗi
            System.err.println("Error fetching transactions for user: " + e.getMessage());
        }

        refreshBorrowReturnTableForUser(userId);
        refreshWaitingBorrowTableForUser(userId);
        updateDashboard(userId);
        generateContributionCalendar(transactions); // Dùng danh sách trống nếu lỗi
    }




    @FXML
    public void initialize() {
        // Liên kết cột với thuộc tính của WaitingBorrow
        waitingDocumentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        waitingDocumentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        waitingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Liên kết cột với BorrowReturn (không thay đổi)
        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        documentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        waitingDocumentIdColumn.prefWidthProperty().bind(waitingBorrowTable.widthProperty().multiply(0.33));
        waitingDocumentTypeColumn.prefWidthProperty().bind(waitingBorrowTable.widthProperty().multiply(0.33));
        waitingStatusColumn.prefWidthProperty().bind(waitingBorrowTable.widthProperty().multiply(0.33));

        // Điều chỉnh kích thước cột BorrowReturn
        membershipIdColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        documentIdColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        documentTypeColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        borrowDateColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        returnDateColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        statusColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));

        waitingBorrowTable.setOnMouseClicked(event -> {
            WaitingBorrow selectedTransaction = waitingBorrowTable.getSelectionModel().getSelectedItem();
            if (selectedTransaction != null && "Waiting".equalsIgnoreCase(selectedTransaction.getStatus())) {
                // Kích hoạt nút Generate QR khi trạng thái là Waiting
                generateQRButton.setDisable(false);
            } else {
                // Vô hiệu hóa nút Generate QR nếu không chọn hoặc trạng thái khác Waiting
                generateQRButton.setDisable(true);
            }
        });

        avatarImageView.setImage(new Image(getClass().getResourceAsStream("/icons/users.png")));
    }

    public void generateContributionCalendar(List<BorrowReturn> transactions) {
        contributionCalendar.getChildren().removeIf(node -> !(node instanceof Label));

        Map<LocalDate, Integer> transactionCounts = new HashMap<>();
        for (BorrowReturn transaction : transactions) {
            LocalDate borrowDate = transaction.getBorrowDate();
            transactionCounts.put(borrowDate, transactionCounts.getOrDefault(borrowDate, 0) + 1);
        }

        LocalDate startOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endOfYear = LocalDate.of(LocalDate.now().getYear(), 12, 31);

        LocalDate currentDate = startOfYear;
        int weekIndex = 1;
        while (!currentDate.isAfter(endOfYear)) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue() % 7; // Java Monday=1 -> Sunday=7

            Rectangle dayCell = new Rectangle(15, 15); // Cell size (15px x 15px)
            dayCell.setArcWidth(5); // Rounded corners
            dayCell.setArcHeight(5);

            int count = transactionCounts.getOrDefault(currentDate, 0);
            dayCell.setFill(getColorForTransactionCount(count));

            Tooltip tooltip = new Tooltip(currentDate + ": " + count + " transactions");
            Tooltip.install(dayCell, tooltip);

            contributionCalendar.add(dayCell, weekIndex, dayOfWeek + 1);

            currentDate = currentDate.plusDays(1);

            if (dayOfWeek == 6) {
                weekIndex++;
            }
        }
    }

    /**
     * Return color based on the number of transactions
     */
    private Color getColorForTransactionCount(int count) {
        if (count == 0) {
            return Color.LIGHTGRAY; // No transactions
        } else if (count <= 5) {
            return Color.LIGHTGREEN; // Few transactions
        } else if (count <= 10) {
            return Color.GREEN; // Moderate
        } else if (count <= 20) {
            return Color.DARKGREEN; // Many
        } else {
            return Color.DARKBLUE; // Very many
        }
    }







    private void loadUserAvatar(String userId) {
        try {
            User user = userDAO.findById(userId);
            if (user != null && user.getAvatar() != null) {
                avatarPath = user.getAvatar();
                avatarImageView.setImage(new Image(new File(avatarPath).toURI().toString()));
            }
        } catch (SQLException e) {
            showError("Error loading avatar: " + e.getMessage());
        }
    }

    private void loadUserInfo(String membershipId) {
        try {
            User user = userDAO.findById(membershipId);
            if (user != null) {
                nameField.setText(user.getName());
                emailField.setText(user.getEmail());
                phoneField.setText(user.getPhone());
            } else {
                showError("User not found.");
                clearUserInfo();
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void enableEdit() {
        // Bật chế độ chỉnh sửa
        nameField.setDisable(false);
        emailField.setDisable(false);
        phoneField.setDisable(false);
        saveButton.setDisable(false);
        editButton.setDisable(true);
    }

    @FXML
    private void saveUserInfo() {
        String newName = nameField.getText();
        String newEmail = emailField.getText();
        String newPhone = phoneField.getText();

        try {
            User updatedUser = new User(userId, newName, newEmail, newPhone);
            userDAO.update(updatedUser);  // Giả sử UserDAO có phương thức updateUser
            showSuccess("User information updated successfully!");

            // Tắt chế độ chỉnh sửa
            nameField.setDisable(true);
            emailField.setDisable(true);
            phoneField.setDisable(true);
            saveButton.setDisable(true);
            editButton.setDisable(false);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void refreshWaitingBorrowTableForUser(String membershipId) {
        try {
            List<WaitingBorrow> waitingTransactions = waitingBorrowDAO.getWaitingTransactionsByUser(membershipId);
            waitingBorrowTable.getItems().setAll(waitingTransactions);
        } catch (SQLException e) {
            showError("Error loading waiting borrow table: " + e.getMessage());
        }
    }


    private void refreshBorrowReturnTableForUser(String membershipId) {
        try {
            List<BorrowReturn> transactions = borrowReturnDAO.getTransactionsByUser(membershipId);
            borrowReturnTable.getItems().setAll(transactions);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void clearUserInfo() {
        nameField.setText("N/A");
        emailField.setText("N/A");
        phoneField.setText("N/A");
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
    private void updateDashboard(String membershipId) {
        try {
            // Get user transactions
            List<BorrowReturn> transactions = borrowReturnDAO.getTransactionsByUser(membershipId);

            // Calculate statistics
            long totalBorrowed = transactions.size();
            long totalReturned = transactions.stream().filter(t -> "Returned".equalsIgnoreCase(t.getStatus())).count();
            long pendingReturns = totalBorrowed - totalReturned;

            // Find the nearest due date
            LocalDate nearestDueDate = transactions.stream()
                    .filter(t -> !"Returned".equalsIgnoreCase(t.getStatus())) // Only pending returns
                    .map(BorrowReturn::getReturnDate)
                    .filter(date -> date != null)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            // Update labels
            totalBorrowedLabel.setText(String.valueOf(totalBorrowed));
            totalReturnedLabel.setText(String.valueOf(totalReturned));
            pendingReturnsLabel.setText(String.valueOf(pendingReturns));

            // Load rank image
            String rankImagePath = getRankImagePath(totalBorrowed);
            Image rankImage = new Image(getClass().getResourceAsStream(rankImagePath));
            if (rankImage.isError()) {
                showError("Unable to load image for rank at path: " + rankImagePath);
            } else {
                rankImageView.setImage(rankImage);
            }

        } catch (SQLException e) {
            showError("Error while updating dashboard: " + e.getMessage());
        }
    }
    // Hàm trả về đường dẫn của ảnh rank dựa trên tổng sách đã mượn
    private String getRankImagePath(long totalBorrowed) {
        if (totalBorrowed < 10) {
            return "/icons/rank1.png";
        } else if (totalBorrowed < 20) {
            return "/icons/rank2.png";
        } else if (totalBorrowed < 50) {
            return "/icons/rank3.png";
        } else if (totalBorrowed < 100) {
            return "/icons/rank4.png";
        } else {
            return "/icons/rank5.png";
        }
    }

    private Object getSelectedDocument() {
        if (waitingBorrowTable.getSelectionModel().getSelectedItem() != null) {
            return waitingBorrowTable.getSelectionModel().getSelectedItem();
        } else if (booksTable.getSelectionModel().getSelectedItem() != null) {
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

    @FXML
    public void generateQR() {
        Object selectedDocument = getSelectedDocument();
        if (selectedDocument == null) {
            showError("No document selected");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save QR Code");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                String qrContent;
                if (selectedDocument instanceof WaitingBorrow waitingBorrow) {
                    if (!"Waiting".equalsIgnoreCase(waitingBorrow.getStatus())) {
                        showError("Only documents with status 'Waiting' can generate QR codes.");
                        return;
                    }
                    qrContent = "Type: " + waitingBorrow.getDocumentType() +
                            ", Document ID: " + waitingBorrow.getDocumentId() +
                            ", Status: " + waitingBorrow.getStatus();
                } else if (selectedDocument instanceof Book book) {
                    qrContent = "Type: BOOK, ID: " + book.getId() + ", Title: " + book.getTitle() +
                            ", Author: " + book.getAuthor() + ", Genre: " + book.getGenre();
                } else if (selectedDocument instanceof Magazine magazine) {
                    qrContent = "Type: MAGAZINE, ID: " + magazine.getId() + ", Title: " + magazine.getTitle() +
                            ", Author: " + magazine.getAuthor() + ", Publisher: " + magazine.getPublisher() +
                            ", Issue: " + magazine.getIssueNumber();
                } else if (selectedDocument instanceof Thesis thesis) {
                    qrContent = "Type: THESIS, ID: " + thesis.getId() + ", Title: " + thesis.getTitle() +
                            ", Author: " + thesis.getAuthor() + ", Supervisor: " + thesis.getSupervisor() +
                            ", University: " + thesis.getUniversity();
                } else if (selectedDocument instanceof BookFromAPI bookFromAPI) {
                    qrContent = "Type: BOOK_FROM_API, ID: " + bookFromAPI.getId() + ", ISBN: " + bookFromAPI.getIsbn() +
                            ", Title: " + bookFromAPI.getTitle() + ", Author: " + bookFromAPI.getAuthor() +
                            ", Publisher: " + bookFromAPI.getPublisher() + ", Published Date: " + bookFromAPI.getPublishedDate();
                } else {
                    showError("Unknown document type");
                    return;
                }

                QRCodeGenerator.generateQRCode(qrContent, file.getAbsolutePath());
                showSuccess("QR Code generated successfully!");
            } catch (Exception e) {
                showError("Error generating QR code: " + e.getMessage());
            }
        }
    }



    @FXML
    private void chooseAvatar() {
        // Open file chooser to select an avatar image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Avatar");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(chooseAvatarButton.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // Load the selected image into the ImageView
                avatarPath = selectedFile.getAbsolutePath();
                Image avatarImage = new Image(selectedFile.toURI().toString());
                avatarImageView.setImage(avatarImage);

                // Enable the save button
                saveAvatarButton.setDisable(false);
            } catch (Exception e) {
                showError("Error loading image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void saveAvatar() {
        if (avatarPath == null || avatarPath.isEmpty()) {
            showError("No avatar selected to save.");
            return;
        }

        try {
            // Update avatar path in the database
            userDAO.updateAvatar(userId, avatarPath);

            // Disable the save button after saving
            saveAvatarButton.setDisable(true);

            showSuccess("Avatar updated successfully!");
        } catch (SQLException e) {
            showError("Error saving avatar: " + e.getMessage());
        }
    }



    @FXML
    private void deleteWaitingDocument() {
        WaitingBorrow selectedTransaction = waitingBorrowTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null || !"Waiting".equals(selectedTransaction.getStatus())) {
            showError("Please select a document with 'Waiting' status.");
            return;
        }

        try {
            // Lấy thông tin documentId và documentType từ tài liệu được chọn
            int documentId = selectedTransaction.getDocumentId();
            String documentType = selectedTransaction.getDocumentType();

            // Xóa tài liệu khỏi danh sách chờ và cập nhật trạng thái trong bảng tương ứng
            waitingBorrowDAO.deleteFromWaitingBorrow(documentId, documentType);

            // Hiển thị thông báo thành công
            showSuccess("Document removed from waiting borrow list and status updated.");

            // Làm mới bảng danh sách chờ
            refreshWaitingBorrowTableForUser(userId);
        } catch (SQLException e) {
            showError("Error deleting document: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Error: " + e.getMessage());
        }
    }


}
