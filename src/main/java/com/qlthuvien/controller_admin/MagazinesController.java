package com.qlthuvien.controller_admin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import com.qlthuvien.dao.MagazineDAO;
import com.qlthuvien.model.Magazine;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MagazinesController {

    private Connection connection;
    private MagazineDAO magazineDAO;

    @FXML
    private TableView<Magazine> magazinesTable;

    @FXML
    private TableColumn<Magazine, Integer> idColumn;
    @FXML
    private TableColumn<Magazine, String> titleColumn;
    @FXML
    private TableColumn<Magazine, String> authorColumn;
    @FXML
    private TableColumn<Magazine, String> publisherColumn;
    @FXML
    private TableColumn<Magazine, Integer> issueNumberColumn;
    @FXML
    private TableColumn<Magazine, String> statusColumn;

    @FXML
    private TextField titleInput, publisherInput, authorInput, issueNumberInput;

    @FXML
    private Label statusLabel;

    public MagazinesController() {
        connection = DBConnection.getConnection();
        magazineDAO = new MagazineDAO(connection);
    }
    
    /**
     * Initializes the controller and sets up the TableView.
     * Configures column widths, cell value factories, and table selection handling.
     * Loads initial magazine data and sets up event handlers.
     */
    @FXML
    public void initialize() {
        // Set column widths based on table size
        idColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.05));
        titleColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.25));
        authorColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.2));
        publisherColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.2));
        issueNumberColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.15));
        statusColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.15));

        // Link columns to Magazine properties
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        issueNumberColumn.setCellValueFactory(new PropertyValueFactory<>("issueNumber"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load initial data from database
        refreshMagazinesTable();

        // Handle event when clicking on a row in the table
        magazinesTable.setOnMouseClicked(event -> {
            Magazine selectedMagazine = magazinesTable.getSelectionModel().getSelectedItem();
            if (selectedMagazine != null) {
                // Populate input fields with selected magazine data
                titleInput.setText(selectedMagazine.getTitle());
                authorInput.setText(selectedMagazine.getAuthor());
                publisherInput.setText(selectedMagazine.getPublisher());
                issueNumberInput.setText(String.valueOf(selectedMagazine.getIssueNumber()));
            }
        });
    }
    
     /**
     * Adds a new magazine to the library system.
     * Creates a new Magazine object from input fields and saves it to the database.
     * Plays a sound effect on successful addition.
     * Updates the table view and shows success/error message.
     * 
     * @throws SQLException if database operation fails
     * @throws NumberFormatException if issue number input is invalid
     * @throws Exception if sound file cannot be played
     */
    @FXML
    public void addMagazine() {
        try {
            // Path to sound file (ensure the path is correct in your project)
            File soundFile = new File("src/main/resources/icons/preview.wav"); // Use .wav file instead of .mp3
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            Magazine newMagazine = new Magazine(0, titleInput.getText(), authorInput.getText(), "available",
                    Integer.parseInt(issueNumberInput.getText()), publisherInput.getText() );
            magazineDAO.add(newMagazine);

            // Play sound when magazine is added successfully
            clip.start();

            showSuccess("Magazine added successfully!");
            refreshMagazinesTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        } catch (NumberFormatException e) {
            showError("Invalid input for issue number");
        } catch (Exception e) {
            showError("Failed to play sound: " + e.getMessage());
        }
    }
    
    /**
     * Updates the selected magazine's information in the database.
     * Takes values from input fields and updates the corresponding magazine record.
     * Shows error message if no magazine is selected.
     * Refreshes the table view after successful update.
     * 
     * @throws SQLException if database operation fails
     * @throws NumberFormatException if issue number input is invalid
     */
    @FXML
    public void editMagazine() {
        Magazine selectedMagazine = magazinesTable.getSelectionModel().getSelectedItem();
        if (selectedMagazine == null) {
            showError("No magazine selected");
            return;
        }

        try {
            selectedMagazine.setTitle(titleInput.getText());
            selectedMagazine.setAuthor(authorInput.getText());
            selectedMagazine.setPublisher(publisherInput.getText());
            selectedMagazine.setIssueNumber(Integer.parseInt(issueNumberInput.getText()));
            magazineDAO.update(selectedMagazine);
            showSuccess("Magazine updated successfully!");
            refreshMagazinesTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        } catch (NumberFormatException e) {
            showError("Invalid input for issue number");
        }
    }
    
    /**
     * Deletes the selected magazine from the library system.
     * Removes the magazine record from the database and updates the table view.
     * Shows error message if no magazine is selected.
     * 
     * @throws SQLException if database operation fails
     */
    @FXML
    public void deleteMagazine() {
        Magazine selectedMagazine = magazinesTable.getSelectionModel().getSelectedItem();
        if (selectedMagazine == null) {
            showError("No magazine selected");
            return;
        }

        try {
            magazineDAO.delete(selectedMagazine.getId());
            showSuccess("Magazine deleted successfully!");
            refreshMagazinesTable();
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }
    
    /**
     * Refreshes the magazines table with current data from the database.
     * Uses DatabaseTask for asynchronous loading to prevent UI freezing.
     * Updates status label to show loading progress.
     */
    private void refreshMagazinesTable() {
        statusLabel.setText("Loading...");

        DatabaseTask.run(
                () -> magazineDAO.getAll(),
                magazines -> {
                    magazinesTable.getItems().setAll(magazines);
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
