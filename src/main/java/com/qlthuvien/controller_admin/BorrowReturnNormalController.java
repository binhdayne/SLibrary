package com.qlthuvien.controller_admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.qlthuvien.dao.BorrowReturnDAO;
import com.qlthuvien.dao.UserDAO;
import com.qlthuvien.dao.WaitingBorrowDAO;
import com.qlthuvien.model.BorrowReturn;
import com.qlthuvien.model.User;
import com.qlthuvien.utils.DBConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

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

    private Connection connection;
    private BorrowReturnDAO borrowReturnDAO;
    private UserDAO userDAO;
    private WaitingBorrowDAO waitingBorrowDAO;

    /**
     * Connect to the database and initialize the DAOs.
     */
    public BorrowReturnNormalController() {
        connection = DBConnection.getConnection();
        borrowReturnDAO = new BorrowReturnDAO(connection);
        userDAO = new UserDAO(connection);
        waitingBorrowDAO = new WaitingBorrowDAO(connection); // Thêm DAO mới
    }

    /**
     * Initializes the controller and sets up the table view columns and document type options.
     * Binds table column widths and sets up property value factories for data display.  
     */
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
    }
    
    /**
     * Checks if the membership ID is valid and retrieves user information.
     */
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

    /**
     * Checks if the document details are valid and retrieves document details.
     */
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
    
    /**
     * Handles the borrowing of a document.
     */
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

            // Check if the document is in 'Waiting' status and belongs to the membership ID
            boolean isWaiting = waitingBorrowDAO.isDocumentWaiting(membershipId, documentId, documentType);
            if (!isWaiting) {
                showError("The document is not in 'Waiting' status or does not belong to this membership ID.");
                return;
            }
            
            // Check if the user has expired waiting documents
            if (waitingBorrowDAO.hasExpiredWaiting(membershipId)) {
                showError("You have expired waiting documents. Please resolve them before borrowing.");
                return;
            }

            // Initialize a new BorrowReturn object
            BorrowReturn newBorrow = new BorrowReturn(
                    membershipId,
                    documentId,
                    documentType,
                    borrowDateInput.getValue(),
                    null, // Return date chưa xác định
                    "Borrowed"
            );

            // Add the new borrow transaction to the borrow_return table
            borrowReturnDAO.borrowDocument(newBorrow);

            // Update the document status in the waiting_borrow table to 'Borrowed'
            waitingBorrowDAO.updateDocumentStatus(documentId, "Borrowed");

            // Display a success message and refresh the table
            showSuccess("Document borrowed successfully!");
            refreshBorrowReturnTableForUser(membershipId);

        } catch (SQLException e) {
            showError("Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Invalid document ID.");
        }
    }

    /**
     * Handles the return of a borrowed document.
     */
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

            selectedTransaction.setReturnDate(returnDate);
            selectedTransaction.setStatus("Returned");
            borrowReturnDAO.returnDocument(selectedTransaction);
            showSuccess("Document returned successfully!");
            refreshBorrowReturnTableForUser(membershipIdInput.getText());
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Refreshes the borrow return table for all users.
     */
    private void refreshBorrowReturnTable() {
        try {
            List<BorrowReturn> transactions = borrowReturnDAO.getAll();
            borrowReturnTable.getItems().setAll(transactions);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Refreshes the borrow return table for a specific user.
     * @param membershipId The membership ID of the user
     */
    private void refreshBorrowReturnTableForUser(String membershipId) {
        try {
            List<BorrowReturn> transactions = borrowReturnDAO.getTransactionsByUser(membershipId);
            borrowReturnTable.getItems().setAll(transactions);
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }
    
    /**
     * Clears user information and disables input fields.
     */
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

    /**
     * Displays an error message dialog.
     * @param message The error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Displays a success message dialog.
     * @param message The success message to display
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
