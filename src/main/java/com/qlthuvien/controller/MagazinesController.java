package com.qlthuvien.controller;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;
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

    /**
     * Constructor MagazineController.
     * Khởi tạo kết nối với cơ sở dữ liệu và đối tượng MagazineDAO.
     */
    public MagazinesController() {
        connection = DBConnection.getConnection();
        magazineDAO = new MagazineDAO(connection);
    }

    /**
     * Khởi tạo initialize() để thiết lập các thuộc tính của giao diện
     */
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


}
