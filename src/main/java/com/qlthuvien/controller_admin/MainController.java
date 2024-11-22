package com.qlthuvien.controller_admin;

import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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

// Tạo biểu đồ cột
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Membership ID");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Số lượt mượn");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Số lượt mượn sách theo thành viên");

// Lấy dữ liệu từ ChartDAO
        Map<String, Integer> borrowCounts = ChartDAO.getBorrowCountsByMembership();

// Sắp xếp dữ liệu theo thứ tự giảm dần và giới hạn 10 mục
        List<Map.Entry<String, Integer>> sortedBorrowCounts = borrowCounts.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Sắp xếp giảm dần
                .limit(10) // Giới hạn 10 mục
                .toList();

// Thêm dữ liệu đã sắp xếp vào biểu đồ
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Lượt mượn");

        for (Map.Entry<String, Integer> entry : sortedBorrowCounts) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChart.getData().add(series);

// Đưa biểu đồ vào ScrollPane
        scrollPane.setContent(barChart);

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
            Node documentManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/HomeScreen.fxml"));
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
            Node documentManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/DocumentManagement.fxml"));
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
            Node userManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/UserManagement.fxml"));
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
            Node borrowReturnManagement = FXMLLoader.load(getClass().getResource("/com/qlthuvien/BorrowReturnManagement.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/qlthuvien/FirstScreen.fxml"));
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
