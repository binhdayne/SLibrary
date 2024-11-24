package com.qlthuvien.controller_admin;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.qlthuvien.dao.ChartDAO;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML
    private VBox contentArea;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ScrollPane scrollPaneHome;
    @FXML
    private Button btnHome, btnDocuments, btnUsers, btnLoans, btnSettings, btnlogout;

    private Button activeButton; // Nút đang được chọn


//    private void fadeInContent() {
//        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), contentArea);
//        fadeTransition.setFromValue(0);
//        fadeTransition.setToValue(1);
//        fadeTransition.play();
//

    @FXML
    public void initialize() {
        setActiveButton(btnHome);
        // Xóa nội dung cũ của ScrollPane
        scrollPane.setContent(null);

        // Tạo biểu đồ cột (BarChart) cho lượt mượn sách theo thành viên
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Membership ID");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượt mượn");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Số lượt mượn sách theo thành viên");

        // Lấy dữ liệu từ ChartDAO
        Map<String, Integer> borrowCounts = ChartDAO.getBorrowCountsByMembership();

        // Sắp xếp dữ liệu theo thứ tự giảm dần và giới hạn 10 mục
        borrowCounts.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sắp xếp giảm dần
                .limit(10) // Giới hạn 10 mục
                .forEach(entry -> {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(entry.getKey());
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                    barChart.getData().add(series);
                });

        // Tạo biểu đồ tròn (PieChart) cho số lượng bản ghi theo bảng
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Số lượng bản ghi theo bảng");

        // Lấy dữ liệu từ ChartDAO
        Map<String, Integer> recordCounts = ChartDAO.getRecordCountsByTable();

        // Thêm dữ liệu vào PieChart
        for (Map.Entry<String, Integer> entry : recordCounts.entrySet()) {
            pieChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // Tạo VBox để chứa cả hai biểu đồ
        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(10));
        contentBox.setAlignment(Pos.CENTER);

        // Thêm các biểu đồ vào VBox
        contentBox.getChildren().addAll(barChart, pieChart);

        // Đặt VBox vào ScrollPane
        scrollPane.setContent(contentBox);

    }

    // Phương thức để đặt nút hiện tại là active và thay đổi giao diện
    private void setActiveButton(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active-button");
        }
        button.getStyleClass().add("active-button");
        activeButton = button;
    }

    // Sự kiện khi nhấn nút Home
    @FXML
    public void showHomeScreen() {
        setActiveButton(btnHome);
        contentArea.getChildren().clear();
        try {
            Node documentManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/view_manager/HomeScreen.fxml"));
            contentArea.getChildren().add(documentManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // Sự kiện khi nhấn nút Documents
    @FXML
    public void showDocumentsScreen() {
        setActiveButton(btnDocuments);
        contentArea.getChildren().clear();
        try {
            Node documentManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/view_manager/DocumentManagement.fxml"));
            contentArea.getChildren().add(documentManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sự kiện khi nhấn nút Users
    @FXML
    public void showUsersScreen() {
        setActiveButton(btnUsers);
        contentArea.getChildren().clear();
        try {
            Node userManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/view_manager/UserManagement.fxml"));
            contentArea.getChildren().add(userManagement);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // Sự kiện khi nhấn nút Loans
    @FXML
    public void showLoansScreen() {
        setActiveButton(btnLoans);
        contentArea.getChildren().clear();
        try {
            Node borrowReturnManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/view_manager/BorrowReturnManagement.fxml"));
            contentArea.getChildren().add(borrowReturnManagement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sự kiện khi nhấn nút Settings
    @FXML
    public void showSettingsScreen() {
        setActiveButton(btnSettings);
        contentArea.getChildren().clear();
        Label settingsLabel = new Label("This is the Settings Screen");
        settingsLabel.setStyle("-fx-font-size: 24px;");
        contentArea.getChildren().add(settingsLabel);
    }

    @FXML
    public void showLogoutScreen() {
        try {
            // Tải giao diện đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/view_manager/FirstScreen.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Lấy Stage hiện tại và thay đổi scene sang màn hình đăng nhập
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}