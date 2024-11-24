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
    private ImageView magazineCoverImageView;
    @FXML
    private Button selectCoverButton;
    @FXML
    private Button editCoverButton;
    @FXML
    private Label coverPathLabel;
    @FXML
    private TextField titleInput, publisherInput, authorInput, issueNumberInput;
    @FXML
    private Label statusLabel;
    @FXML
    private Button generateQRButton;
    @FXML
    private VBox starContainer;

    public MagazinesController() {
        connection = DBConnection.getConnection();
        magazineDAO = new MagazineDAO(connection);
    }

    @FXML
    public void initialize() {
        // Set column widths
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

        // Handle table row selection
        magazinesTable.setOnMouseClicked(event -> {
            Magazine selectedMagazine = magazinesTable.getSelectionModel().getSelectedItem();
            if (selectedMagazine != null) {
                // Populate input fields
                titleInput.setText(selectedMagazine.getTitle());
                authorInput.setText(selectedMagazine.getAuthor());
                publisherInput.setText(selectedMagazine.getPublisher());
                issueNumberInput.setText(String.valueOf(selectedMagazine.getIssueNumber()));

                // Display the cover and enable buttons
                displayMagazineCover(selectedMagazine.getCoverPath());
                coverPathLabel.setText(selectedMagazine.getCoverPath());
                generateQRButton.setDisable(false);
                editCoverButton.setDisable(false);
            } else {
                clearFields();
                magazineCoverImageView.setImage(null);
                coverPathLabel.setText("");
                generateQRButton.setDisable(true);
                editCoverButton.setDisable(true);
            }
        });



        // Create star animation
        if (starContainer != null) {
            Platform.runLater(() -> {
                StarAnimationUtil.createStarAnimation(starContainer);
            });
        }

        // Add event handlers for cover selection and editing
        selectCoverButton.setOnAction(event -> selectMagazineCover());
        editCoverButton.setOnAction(event -> editMagazineCover());
    }

    @FXML
    private void selectMagazineCover() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Magazine Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            coverPathLabel.setText(selectedFile.getAbsolutePath());
            displayMagazineCover(selectedFile.getAbsolutePath()); // Preview the selected cover
        } else {
            coverPathLabel.setText(""); // Clear if no file is selected
        }
    }

    private void clearFields() {
        titleInput.clear();
        authorInput.clear();
        publisherInput.clear();
        issueNumberInput.clear();
        coverPathLabel.setText("No file selected");
        magazineCoverImageView.setImage(null);
        editCoverButton.setDisable(true); // Disable Edit Cover button
    }


    @FXML
    private void editMagazineCover() {
        Magazine selectedMagazine = magazinesTable.getSelectionModel().getSelectedItem();
        if (selectedMagazine == null) {
            showError("No magazine selected to edit the cover.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select New Magazine Cover Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            String newCoverPath = selectedFile.getAbsolutePath();
            selectedMagazine.setCoverPath(newCoverPath); // Update the magazine's cover path

            try {
                magazineDAO.update(selectedMagazine); // Save the updated cover path to the database
                showSuccess("Magazine cover updated successfully!");
                displayMagazineCover(newCoverPath); // Refresh the cover preview
                coverPathLabel.setText(newCoverPath); // Update the label with the new path
            } catch (SQLException e) {
                showError("Error updating magazine cover: " + e.getMessage());
            }
        } else {
            showError("No file selected for the cover.");
        }
    }


    private void displayMagazineCover(String coverPath) {
        if (coverPath != null && !coverPath.isEmpty()) {
            File coverFile = new File(coverPath);
            if (coverFile.exists()) {
                magazineCoverImageView.setImage(new javafx.scene.image.Image(coverFile.toURI().toString()));
            } else {
                magazineCoverImageView.setImage(null); // Clear the view if the file does not exist
            }
        } else {
            magazineCoverImageView.setImage(null); // Clear the view if no cover path is set
        }
    }


    @FXML
    public void addMagazine() {
        try {
            // Path to sound file (ensure the path is correct in your project)
            File soundFile = new File("src/main/resources/icons/preview.wav"); // Use .wav file instead of .mp3
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            String bookCoverPath = coverPathLabel.getText();
            Magazine newMagazine = new Magazine(0, titleInput.getText(), authorInput.getText(), "available",
                    Integer.parseInt(issueNumberInput.getText()), publisherInput.getText(), bookCoverPath );
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

    @FXML
    public void generateQR() {
        Magazine selectedMagazine = magazinesTable.getSelectionModel().getSelectedItem();
        if (selectedMagazine == null) {
            showError("No magazine selected");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save QR Code");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                String qrContent = "Type: MAGAZINE, ID: " + selectedMagazine.getId() +
                        ", Title: " + selectedMagazine.getTitle() +
                        ", Author: " + selectedMagazine.getAuthor() +
                        ", Publisher: " + selectedMagazine.getPublisher() +
                        ", Issue: " + selectedMagazine.getIssueNumber();
                QRCodeGenerator.generateQRCode(qrContent, file.getAbsolutePath());
                showSuccess("QR Code generated successfully!");
            } catch (Exception e) {
                showError("Error generating QR code: " + e.getMessage());
            }
        }
    }
}