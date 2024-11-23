package com.qlthuvien.controller_admin;

import com.qlthuvien.dao.ChartDAO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class HomeController {

    @FXML
    private ScrollPane scrollPaneHome;

    @FXML
    public void initialize() {
        // Xóa nội dung cũ của ScrollPane
        scrollPaneHome.setContent(null);

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
        scrollPaneHome.setContent(contentBox);
    }
}
