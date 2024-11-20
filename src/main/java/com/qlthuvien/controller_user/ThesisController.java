package com.qlthuvien.controller_user;

import com.qlthuvien.dao.ThesisDAO;
import com.qlthuvien.model.Thesis;
import com.qlthuvien.utils.DBConnection;
import com.qlthuvien.utils.DatabaseTask;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;

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

    public ThesisController() {
        connection = DBConnection.getConnection();
        thesisDAO = new ThesisDAO(connection);
    }

    @FXML
    public void initialize() {
        // Đặt tỷ lệ chiều rộng cho các cột dựa trên tổng chiều rộng của bảng
        idColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.05));
        titleColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.25));
        authorColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.2));
        supervisorColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.2));
        universityColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.2));
        statusColumn.prefWidthProperty().bind(thesesTable.widthProperty().multiply(0.1));

        // Liên kết các cột với thuộc tính của Thesis
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        supervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisor"));
        universityColumn.setCellValueFactory(new PropertyValueFactory<>("university"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Tải dữ liệu ban đầu từ cơ sở dữ liệu
        refreshThesesTable();

        // Xử lý sự kiện khi nhấn vào một dòng trong bảng
        thesesTable.setOnMouseClicked(event -> {
            Thesis selectedThesis = thesesTable.getSelectionModel().getSelectedItem();
            if (selectedThesis != null) {
                // Đổ dữ liệu từ dòng được chọn lên các ô nhập liệu
                titleInput.setText(selectedThesis.getTitle());
                authorInput.setText(selectedThesis.getAuthor());
                supervisorInput.setText(selectedThesis.getSupervisor());
                universityInput.setText(selectedThesis.getUniversity());
            }
        });
    }

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
