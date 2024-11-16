package com.qlthuvien.controller_admin;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

import com.qlthuvien.dao.MagazineDAO;
import com.qlthuvien.model.Magazine;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;

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

    @FXML
    public void initialize() {
        // Đặt tỷ lệ chiều rộng cho các cột dựa trên tổng chiều rộng của bảng
        idColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.05));
        titleColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.25));
        authorColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.2));
        publisherColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.2));
        issueNumberColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.15));
        statusColumn.prefWidthProperty().bind(magazinesTable.widthProperty().multiply(0.15));

        // Liên kết các cột với thuộc tính của Magazine
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        issueNumberColumn.setCellValueFactory(new PropertyValueFactory<>("issueNumber"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load dữ liệu ban đầu từ cơ sở dữ liệu
        refreshMagazinesTable();

        // Xử lý sự kiện khi nhấn vào một dòng trong bảng
        magazinesTable.setOnMouseClicked(event -> {
            Magazine selectedMagazine = magazinesTable.getSelectionModel().getSelectedItem();
            if (selectedMagazine != null) {
                // Đổ dữ liệu từ dòng được chọn lên các ô nhập liệu
                titleInput.setText(selectedMagazine.getTitle());
                authorInput.setText(selectedMagazine.getAuthor());
                publisherInput.setText(selectedMagazine.getPublisher());
                issueNumberInput.setText(String.valueOf(selectedMagazine.getIssueNumber()));
            }
        });
    }

    @FXML
    public void addMagazine() {
        try {
            // Đường dẫn đến file âm thanh (đảm bảo đường dẫn là chính xác trong dự án của bạn)
            File soundFile = new File("src/main/resources/icons/preview.wav"); // Sử dụng file .wav thay vì .mp3
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            Magazine newMagazine = new Magazine(0, titleInput.getText(), authorInput.getText(), "available",
                    Integer.parseInt(issueNumberInput.getText()), publisherInput.getText() );
            magazineDAO.add(newMagazine);

            // Phát âm thanh khi thêm tạp chí thành công
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
}
