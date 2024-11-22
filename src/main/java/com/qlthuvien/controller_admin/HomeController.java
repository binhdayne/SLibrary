package com.qlthuvien.controller_admin;

import com.qlthuvien.dao.ChartDAO;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;

import java.util.List;
import java.util.Map;


public class HomeController {

    @FXML
    private ScrollPane scrollPaneHome;

    @FXML
    public void initialize() {

        // Xóa nội dung cũ của ScrollPane
        scrollPaneHome.setContent(null);

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
        scrollPaneHome.setContent(barChart);
    }
}
