package com.qlthuvien.controller_admin;

import java.sql.Connection;
import java.sql.SQLException;

import com.qlthuvien.dao.ThesisDAO;
import com.qlthuvien.model.Thesis;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ThesisController {

    private Connection connection;
    private ThesisDAO thesisDAO;

    @FXML
    private TableView<Thesis> thesesTable;

    @FXML
    private TableColumn<Thesis, Integer> idColumn;
    @FXML
    private TableColumn<Thesis, String> titleColumn;
    @FXML
    private TableColumn<Thesis, String> authorColumn;
    @FXML
    private TableColumn<Thesis, String> supervisorColumn;
    @FXML
    private TableColumn<Thesis, String> universityColumn;
    @FXML
    private TableColumn<Thesis, String> statusColumn;

    @FXML
    private TextField titleInput, authorInput, supervisorInput, universityInput;
    @FXML
    private Label statusLabel;
    
    /**
     * Constructor for ThesisController.
     * Initializes database connection and ThesisDAO instance.
     */
    public ThesisController() {
        connection = DBConnection.getConnection();
        thesisDAO = new ThesisDAO(connection);
    }
    
    /**
     * Initializes the controller and sets up the TableView.
     * Configures column widths as percentages of table width,
     * sets up cell value factories, and initializes table selection handling.
     * Loads initial thesis data from database.
     */
    @FXML
    public void initialize() {
        // Set column widths based on table size
        idColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.05));
        titleColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.25));
        authorColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.2));
        supervisorColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.2));
        universityColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.2));
        statusColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.1));

        // Link columns to Thesis properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        supervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisor"));
        universityColumn.setCellValueFactory(new PropertyValueFactory<>("university"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load initial data from database
        refreshThesesTable();

        // Handle event when clicking on a row in the table
        thesesTable.setOnMouseClicked(event -> {
            Thesis selectedThesis = thesesTable.getSelectionModel().getSelectedItem();
            if (selectedThesis != null) {
                // Populate input fields with selected thesis data
                titleInput.setText(selectedThesis.getTitle());
                authorInput.setText(selectedThesis.getAuthor());
                supervisorInput.setText(selectedThesis.getSupervisor());
                universityInput.setText(selectedThesis.getUniversity());
            }
        });
    }
    
    /**
     * Adds a new thesis to the library system.
     * Creates a new Thesis object from input fields and saves it to the database.
     * Sets default status as "available" for new theses.
     * Updates the table view and shows success/error message.
     * 
     * @throws SQLException if database operation fails
     */
    @FXML
    public void addThesis() {
        try {
            Thesis newThesis = new Thesis(0, titleInput.getText(), authorInput.getText(),
                    supervisorInput.getText(), universityInput.getText(), "available");
            thesisDAO.add(newThesis);
            showSuccess("Thesis added successfully!");
            refreshThesesTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }
    
    /**
     * Updates the selected thesis's information in the database.
     * Takes values from input fields and updates the corresponding thesis record.
     * Shows error message if no thesis is selected.
     * Refreshes the table view after successful update.
     * 
     * @throws SQLException if database operation fails
     */
    @FXML
    public void editThesis() {
        Thesis selectedThesis = thesesTable.getSelectionModel().getSelectedItem();
        if (selectedThesis == null) {
            showError("No thesis selected");
            return;
        }

        try {
            selectedThesis.setTitle(titleInput.getText());
            selectedThesis.setAuthor(authorInput.getText());
            selectedThesis.setSupervisor(supervisorInput.getText());
            selectedThesis.setUniversity(universityInput.getText());
            thesisDAO.update(selectedThesis);
            showSuccess("Thesis updated successfully!");
            refreshThesesTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    /**
     * Deletes the selected thesis from the library system.
     * Removes the thesis record from the database and updates the table view.
     * Shows error message if no thesis is selected.
     * 
     * @throws SQLException if database operation fails
     */
    @FXML
    public void deleteThesis() {
        Thesis selectedThesis = thesesTable.getSelectionModel().getSelectedItem();
        if (selectedThesis == null) {
            showError("No thesis selected");
            return;
        }

        try {
            thesisDAO.delete(selectedThesis.getId());
            showSuccess("Thesis deleted successfully!");
            refreshThesesTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }
    
    /**
     * Refreshes the theses table with current data from the database.
     * Uses DatabaseTask for asynchronous loading to prevent UI freezing.
     * Updates status label to show loading progress and completion.
     */
    private void refreshThesesTable() {
        statusLabel.setText("Loading...");

        DatabaseTask.run(
                () -> thesisDAO.getAll(),
                theses -> {
                    thesesTable.getItems().setAll(theses);
                    statusLabel.setText("Load complete!");
                },
                exception -> {
                    showError(exception.getMessage());
                    statusLabel.setText("Failed to load data");
                }
        );
    }
    
    /**
     * Displays an error message dialog to the user.
     * 
     * @param message The error message to be displayed
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
    
    /**
     * Displays a success message dialog to the user.
     * 
     * @param message The success message to be displayed
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}
