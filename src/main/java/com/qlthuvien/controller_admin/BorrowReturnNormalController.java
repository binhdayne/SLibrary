package com.qlthuvien.controller_admin;

import java.security.cert.PolicyNode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.qlthuvien.dao.BorrowReturnDAO;
import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.dao.WaitingBorrowDAO;
import com.qlthuvien.model.BorrowReturn;
import com.qlthuvien.model.User;
import com.qlthuvien.model.WaitingBorrow;
import com.qlthuvien.utils.DBConnection;
//import com.qlthuvien.utils.StarAnimationUtil;

import com.qlthuvien.utils.StarAnimationUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class BorrowReturnNormalController {

    @FXML
    private TableView<BorrowReturn> borrowReturnTable;

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
    private TableView<WaitingBorrow> waitingBorrowTable;

    @FXML
    private TableColumn<WaitingBorrow, String> membershipIdWaitingColumn;
    @FXML
    private TableColumn<WaitingBorrow, String> documentIdWaitingColumn;
    @FXML
    private TableColumn<WaitingBorrow, String> documentTypeWaitingColumn;
    @FXML
    private TableColumn<WaitingBorrow, LocalDate> borrowDateWaitingColumn;
    @FXML
    private TableColumn<WaitingBorrow, String> statusWaitingColumn;

    @FXML
    private TextField membershipIdInput, documentIdInput;
    @FXML
    private ComboBox<String> documentTypeInput;
    @FXML
    private Label nameLabel, emailLabel, phoneLabel, documentDetailsLabel;
    @FXML
    private DatePicker borrowDateInput, returnDateInput;
    @FXML
    private Button borrowButton, returnButton;
    @FXML
    private GridPane documentFormPane;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private VBox starContainer;

    private Connection connection;
    private BorrowReturnDAO borrowReturnDAO;
    private UserDAO userDAO;
    private WaitingBorrowDAO waitingBorrowDAO;
    @FXML
    private VBox waitingVBox;
    public BorrowReturnNormalController() {
        connection = DBConnection.getConnection();
        borrowReturnDAO = new BorrowReturnDAO(connection);
        userDAO = new UserDAO(connection);
        waitingBorrowDAO = new WaitingBorrowDAO(connection); // Add new DAO
    }

    @FXML
    public void initialize() {
        // Initialize ComboBox for document type
        ObservableList<String> documentTypes = FXCollections.observableArrayList("BOOK", "MAGAZINE", "THESIS", "BOOK_FROM_API");
        documentTypeInput.setItems(documentTypes);

        // Set column widths and property bindings
        membershipIdColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        documentIdColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        documentTypeColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        borrowDateColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        returnDateColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));
        statusColumn.prefWidthProperty().bind(borrowReturnTable.widthProperty().multiply(0.15));

        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        documentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        returnDateColumn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        refreshBorrowReturnTable();

        // Lấy dữ liệu từ DAO
        List<Map<String, String>> waitingItems = BorrowReturnDAO.getWaitingBorrowedItems();

// Tạo TableView
        TableView<Map<String, String>> tableView = new TableView<>();

// Tạo các cột với Callback
        TableColumn<Map<String, String>, String> membershipIdColumn = new TableColumn<>("Membership ID");
        membershipIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get("membership_id"))
        );

        TableColumn<Map<String, String>, String> documentIdColumn = new TableColumn<>("Document ID");
        documentIdColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get("document_id"))
        );

        TableColumn<Map<String, String>, String> documentTypeColumn = new TableColumn<>("Document Type");
        documentTypeColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get("document_type"))
        );

        TableColumn<Map<String, String>, String> borrowDateColumn = new TableColumn<>("Borrow Date");
        borrowDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get("borrow_date"))
        );

        TableColumn<Map<String, String>, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().get("status"))
        );

// Thêm các cột vào TableView
        tableView.getColumns().addAll(membershipIdColumn, documentIdColumn, documentTypeColumn, borrowDateColumn, statusColumn);

// Thêm dữ liệu vào TableView
        ObservableList<Map<String, String>> observableList = FXCollections.observableArrayList(waitingItems);
        tableView.setItems(observableList);

// Thêm TableView vào VBox
        waitingVBox.getChildren().add(tableView);
        // Create Star animation
        if (starContainer != null) {
            Platform.runLater(() -> {
                StarAnimationUtil.createStarAnimation(starContainer);
            });
        }
    }
    @FXML
    private void checkMembershipId() {
        try {
            String membershipId = membershipIdInput.getText();
            User user = userDAO.findById(membershipId);
            if (user != null) {
                nameLabel.setText(user.getName());
                emailLabel.setText(user.getEmail());
                phoneLabel.setText(user.getPhone());

                documentIdInput.setDisable(false);
                documentTypeInput.setDisable(false);
                borrowDateInput.setDisable(false);
                returnDateInput.setDisable(false);
                borrowButton.setDisable(false);
                returnButton.setDisable(false);

                refreshBorrowReturnTableForUser(membershipId);
            } else {
                showError("User not found.");
                clearUserInfo();
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void checkDocumentDetails() {
        try {
            String documentType = documentTypeInput.getValue();
            int documentId = Integer.parseInt(documentIdInput.getText());

            // Retrieve document details based on the selected document type and ID
            String details = borrowReturnDAO.getDocumentDetails(documentType, documentId);
            if (details != null) {
                documentDetailsLabel.setText(details);
            } else {
                showError("Document not found.");
                documentDetailsLabel.setText("N/A");
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        } catch (NumberFormatException e) {
            showError("Invalid document ID.");
        }
    }

    @FXML
    public void borrowDocument() {
        if (membershipIdInput.getText().isEmpty() ||
                documentIdInput.getText().isEmpty() ||
                documentTypeInput.getValue() == null ||
                borrowDateInput.getValue() == null) {

            showError("Please fill in all the required fields.");
            return;
        }

        LocalDate returnDate = returnDateInput.getValue();
        if (returnDate != null && returnDate.isBefore(borrowDateInput.getValue())) {
            showError("Return date must be after the borrow date.");
            return;
        }

        try {
            String membershipId = membershipIdInput.getText();
            int documentId = Integer.parseInt(documentIdInput.getText());
            String documentType = documentTypeInput.getValue();

            // Check the 'Waiting' status in the waiting_borrow table
            boolean isWaiting = waitingBorrowDAO.isDocumentWaiting(membershipId, documentId, documentType);
            if (!isWaiting) {
                showError("The document is not in 'Waiting' status or does not belong to this membership ID.");
                return;
            }

            if (waitingBorrowDAO.hasExpiredWaiting(membershipId)) {
                showError("You have expired waiting documents. Please resolve them before borrowing.");
                return;
            }

            // Create a BorrowReturn object
            BorrowReturn newBorrow = new BorrowReturn(
                    membershipId,
                    documentId,
                    documentType,
                    borrowDateInput.getValue(),
                    null, // Return date is not determined
                    "Borrowed"
            );

            // Add to the borrow_return table via DAO
            borrowReturnDAO.borrowDocument(newBorrow);

            // Update the status in the waiting_borrow table to 'Borrowed'
            waitingBorrowDAO.updateDocumentStatus(documentId, "Borrowed");

            // Show success message and refresh the table
            showSuccess("Document borrowed successfully!");
            refreshBorrowReturnTableForUser(membershipId);

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Invalid document ID.");
        }
    }



    @FXML
    public void returnDocument() {
        BorrowReturn selectedTransaction = borrowReturnTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null || !"Borrowed".equals(selectedTransaction.getStatus())) {
            showError("Please select a borrowed document.");
            return;
        }

        try {
            LocalDate returnDate = returnDateInput.getValue();
            if (returnDate == null) {
                showError("Please select a return date.");
                return;
            }

            // Cập nhật thông tin trả sách
            selectedTransaction.setReturnDate(returnDate);
            selectedTransaction.setStatus("Returned");
            borrowReturnDAO.returnDocument(selectedTransaction);

            // Xóa sách khỏi bảng waiting_borrow
            waitingBorrowDAO.deleteFromWaitingBorrow(
                    selectedTransaction.getDocumentId(),
                    selectedTransaction.getDocumentType());

            // Thông báo thành công
            showSuccess("Document returned successfully!");

            // Làm mới bảng
            refreshBorrowReturnTableForUser(membershipIdInput.getText());
            refreshBorrowReturnTableForUser(membershipIdInput.getText()); // Làm mới bảng WaitingBorrow
        } catch (SQLException e) {
            showError("Error: " + e.getMessage());
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
        nameLabel.setText("N/A");
        emailLabel.setText("N/A");
        phoneLabel.setText("N/A");
        documentDetailsLabel.setText("N/A");

        documentIdInput.setDisable(true);
        documentTypeInput.setDisable(true);
        borrowDateInput.setDisable(true);
        returnDateInput.setDisable(true);
        borrowButton.setDisable(true);
        returnButton.setDisable(true);
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
