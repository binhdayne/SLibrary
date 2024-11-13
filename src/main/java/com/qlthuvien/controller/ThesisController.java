package com.qlthuvien.controller;

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

    /**
     * Constructor ThesisController.
     * Khởi tạo kết nối của database và đối tượng ThesisDAO.
     */
    public ThesisController() {
        connection = DBConnection.getConnection();
        thesisDAO = new ThesisDAO(connection);
    }

    /**
     * Khởi tạo initialize() để thiết lập các thuộc tính của giao diện
     */
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

        // Load dữ liệu từ cơ sở dữ liệu lên bảng

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




}
