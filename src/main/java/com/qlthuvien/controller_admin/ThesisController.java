package com.qlthuvien.controller_admin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import com.qlthuvien.dao.ThesisDAO;
import com.qlthuvien.model.Thesis;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import com.qlthuvien.utils.QRCodeGenerator;

import com.qlthuvien.utils.StarAnimationUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    private ImageView thesisCoverImageView;
    @FXML
    private Button editCoverButton;
    @FXML
    private Label coverPathLabel;
    @FXML
    private TextField titleInput, authorInput, supervisorInput, universityInput;
    @FXML
    private Label statusLabel;
    @FXML
    private Button generateQRButton;
    @FXML
    private Button selectCoverButton;
    @FXML
    private VBox starContainer;
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
        // Set up columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        supervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisor"));
        universityColumn.setCellValueFactory(new PropertyValueFactory<>("university"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load theses into the table
        refreshThesesTable();

        // Handle table selection
        thesesTable.setOnMouseClicked(event -> {
            Thesis selectedThesis = thesesTable.getSelectionModel().getSelectedItem();
            if (selectedThesis != null) {
                // Populate input fields
                titleInput.setText(selectedThesis.getTitle());
                authorInput.setText(selectedThesis.getAuthor());
                supervisorInput.setText(selectedThesis.getSupervisor());
                universityInput.setText(selectedThesis.getUniversity());
                coverPathLabel.setText(selectedThesis.getCoverPath()); // Display the cover path
                displayThesisCover(selectedThesis.getCoverPath());     // Display the cover image
                editCoverButton.setDisable(false); // Enable the Edit Cover button
                generateQRButton.setDisable(false);
            } else {
                clearFields();
            }
        });

        // Create star animation
        if (starContainer != null) {
            Platform.runLater(() -> {
                StarAnimationUtil.createStarAnimation(starContainer);
            });
        }

//        selectCoverButton.setOnAction(event -> selectThesisCover());
        editCoverButton.setOnAction(event -> editThesisCover());
    }

    @FXML
    private void selectThesisCover() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Magazine Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            coverPathLabel.setText(selectedFile.getAbsolutePath());
            displayThesisCover(selectedFile.getAbsolutePath()); // Preview the selected cover
        } else {
            coverPathLabel.setText(""); // Clear if no file is selected
        }
    }

    @FXML
    private void editThesisCover() {
        Thesis selectedThesis = thesesTable.getSelectionModel().getSelectedItem();
        if (selectedThesis == null) {
            showError("No thesis selected to edit the cover.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Thesis Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            String newCoverPath = selectedFile.getAbsolutePath();
            selectedThesis.setCoverPath(newCoverPath); // Update the cover path

            try {
                thesisDAO.update(selectedThesis); // Save changes to the database
                showSuccess("Thesis cover updated successfully!");
                displayThesisCover(newCoverPath); // Refresh the cover display
                coverPathLabel.setText(newCoverPath); // Update the label
            } catch (SQLException e) {
                showError("Error updating thesis cover: " + e.getMessage());
            }
        } else {
            showError("No file selected for the cover.");
        }
    }

    private void displayThesisCover(String coverPath) {
        if (coverPath != null && !coverPath.isEmpty()) {
            File coverFile = new File(coverPath);
            if (coverFile.exists()) {
                thesisCoverImageView.setImage(new javafx.scene.image.Image(coverFile.toURI().toString()));
            } else {
                thesisCoverImageView.setImage(null); // Clear the view if the file does not exist
            }
        } else {
            thesisCoverImageView.setImage(null); // Clear the view if no cover path is set
        }
    }

    private void clearFields() {
        titleInput.clear();
        authorInput.clear();
        supervisorInput.clear();
        universityInput.clear();
        coverPathLabel.setText("No file selected");
        thesisCoverImageView.setImage(null);
        editCoverButton.setDisable(true);
        generateQRButton.setDisable(true);
    }

    @FXML
    public void addThesis() {
        String bookCoverPath = coverPathLabel.getText();
        try {
            Thesis newThesis = new Thesis(0, titleInput.getText(), authorInput.getText(),
                    supervisorInput.getText(), universityInput.getText(), "available", bookCoverPath);
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

    /**
     * Generates a QR code for the selected thesis.
     * Shows a file chooser dialog to save the QR code as a PNG file.
     * Uses QRCodeGenerator to generate the QR code and save it to the specified file.
     * Shows success/error message based on the result of the QR code generation.
     */
    @FXML
    public void generateQR() {
        Thesis selectedThesis = thesesTable.getSelectionModel().getSelectedItem();
        if (selectedThesis == null) {
            showError("No thesis selected");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save QR Code");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                String qrContent = "Type: THESIS, ID: " + selectedThesis.getId() +
                        ", Title: " + selectedThesis.getTitle() +
                        ", Author: " + selectedThesis.getAuthor() +
                        ", Supervisor: " + selectedThesis.getSupervisor() +
                        ", University: " + selectedThesis.getUniversity();
                QRCodeGenerator.generateQRCode(qrContent, file.getAbsolutePath());
                showSuccess("QR Code generated successfully!");
            } catch (Exception e) {
                showError("Error generating QR code: " + e.getMessage());
            }
        }
    }
}