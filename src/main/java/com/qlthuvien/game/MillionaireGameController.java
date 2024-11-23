package com.qlthuvien.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MillionaireGameController {

    @FXML
    private Label lblQuestion;

    @FXML
    private Button btnAnswerA, btnAnswerB, btnAnswerC, btnAnswerD;

    // Danh sách tất cả câu hỏi (50 câu)
    private List<String[]> allQuestions = List.of(
            new String[]{"Thủ đô của Việt Nam là gì?", "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Huế", "A"},
            new String[]{"Tác giả của Truyện Kiều là ai?", "Nguyễn Trãi", "Nguyễn Du", "Hồ Xuân Hương", "Ngô Thì Nhậm", "B"},
            new String[]{"Thư viện Quốc gia Việt Nam nằm ở thành phố nào?", "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Cần Thơ", "A"},
            new String[]{"Hệ thống thư viện của Đại học Quốc gia Hà Nội sử dụng phần mềm nào để quản lý?", "Libol", "Dspace", "Aleph", "Koha", "A"},
            new String[]{"Thư viện trường đại học thường phân loại sách theo hệ thống nào?", "Dewey", "LC", "UDC", "ISBN", "B"},
            new String[]{"Mã ISBN dùng để định danh điều gì?", "Tác giả", "Nhà xuất bản", "Quyển sách", "Chuyên ngành", "C"},
            new String[]{"DSpace là phần mềm dùng để làm gì?", "Quản lý thư viện số", "Soạn thảo văn bản", "Phát triển website", "Dịch tài liệu", "A"},
            new String[]{"Tác phẩm nào của Nguyễn Trãi được lưu giữ trong thư viện quốc gia?", "Bình Ngô Đại Cáo", "Hồng Đức Bản Đồ", "Truyền Kỳ Mạn Lục", "Chinh Phụ Ngâm", "A"},
            new String[]{"Tập hợp các bài báo khoa học được lưu trữ trực tuyến thường gọi là gì?", "Tạp chí", "Kho lưu trữ số", "Cơ sở dữ liệu", "Bộ sưu tập", "C"},
            new String[]{"Open Access (OA) trong thư viện có nghĩa là gì?", "Truy cập mở", "Truy cập trả phí", "Truy cập giới hạn", "Không truy cập được", "A"},
            new String[]{"Thư viện số Đại học Quốc gia Hà Nội có tên là gì?", "VNU Library", "VINELib", "Dspace VNU", "HanuLib", "B"},
            new String[]{"Thư viện nào được xem là lớn nhất thế giới?", "Thư viện Quốc gia Mỹ", "Thư viện Anh Quốc", "Thư viện Quốc hội Mỹ", "Thư viện Quốc gia Nga", "C"},
            new String[]{"Định dạng tài liệu PDF thường được sử dụng cho mục đích gì?", "In ấn", "Lập trình", "Lưu trữ và đọc tài liệu", "Quản lý cơ sở dữ liệu", "C"},
            new String[]{"Tài liệu tham khảo phải tuân theo hệ thống trích dẫn nào?", "IEEE", "APA", "Chicago", "Tùy loại tài liệu", "D"},
            new String[]{"DOI (Digital Object Identifier) dùng để định danh điều gì?", "Tài liệu số", "Tác giả", "Nhà xuất bản", "Hệ thống phân loại", "A"},
            new String[]{"Tác phẩm \"Đất Rừng Phương Nam\" thuộc thể loại nào?", "Tiểu thuyết", "Hồi ký", "Truyện ngắn", "Thơ", "A"},
            new String[]{"\"Dế Mèn Phiêu Lưu Ký\" là tác phẩm của ai?", "Tô Hoài", "Nguyễn Tuân", "Nguyễn Nhật Ánh", "Vũ Trọng Phụng", "A"},
            new String[]{"CSDL nào thường lưu trữ các bài báo khoa học?", "PubMed", "SciFinder", "Web of Science", "Cả ba đều đúng", "D"},
            new String[]{"OCLC là tổ chức gì?", "Thư viện trực tuyến", "Tổ chức thư viện toàn cầu", "Nhà xuất bản sách", "Kho lưu trữ mở", "B"},
            new String[]{"Thẻ thư viện giúp sinh viên làm gì?", "Mượn sách", "Đăng ký môn học", "Tra cứu bài giảng", "Cả ba đều đúng", "A"},
            new String[]{"Lý thuyết Dewey (Dewey Decimal Classification) dùng để làm gì?", "Phân loại sách", "Đánh giá tài liệu", "Quản lý nhân viên", "Sắp xếp tủ sách", "A"},
            new String[]{"Tác giả nào nổi tiếng với tập truyện \"Những Ngôi Sao Xa Xôi\"?", "Nguyễn Quang Sáng", "Nguyễn Minh Châu", "Lê Minh Khuê", "Nam Cao", "C"},
            new String[]{"Kho tài liệu tham khảo điện tử tại ĐHQG thường bao gồm gì?", "Sách điện tử", "Tài liệu PDF", "Cơ sở dữ liệu", "Cả ba đều đúng", "D"},
            new String[]{"\"Hệ thống quản lý thư viện số VINELib\" hỗ trợ sinh viên bằng cách nào?", "Mượn sách trực tuyến", "Tìm kiếm tài liệu", "Đăng ký tài khoản thư viện", "Cả ba đều đúng", "D"},
            new String[]{"Thư viện có thể tổ chức gì để hỗ trợ học tập?", "Hội thảo", "Đào tạo kỹ năng tra cứu", "Câu lạc bộ đọc sách", "Cả ba đều đúng", "D"},
            new String[]{"\"Thư viện Quốc hội Mỹ\" lưu trữ loại tài liệu nào?", "Sách", "Bản đồ", "Bản ghi âm", "Cả ba đều đúng", "D"},
            new String[]{"Từ điển 'Vietnamese-English Dictionary' được biên soạn bởi ai?", "Đào Duy Anh", "Nguyễn Quang Hồng", "Trần Văn Chánh", "Lê Quang Đạo", "B"},
            new String[]{"Mục đích của thư viện Đại học Quốc gia Hà Nội là gì?", "Cung cấp tài liệu nghiên cứu cho sinh viên và giảng viên", "Cung cấp sách tham khảo", "Cung cấp tài liệu chuyên ngành", "Tất cả các phương án trên", "D"},
            new String[]{"Tác phẩm \"Lão Hạc\" là của ai?", "Nguyễn Du", "Nam Cao", "Tô Hoài", "Ngô Tất Tố", "B"}

    );

    private List<String[]> selectedQuestions; // Danh sách 10 câu được chọn ngẫu nhiên
    private int currentQuestionIndex = 0;
    private int score = 0; // Biến lưu điểm số

    @FXML
    public void initialize() {
        selectRandomQuestions();
        loadQuestion();
    }

    private void selectRandomQuestions() {
        // Tạo một danh sách mới từ allQuestions và xáo trộn
        List<String[]> shuffledQuestions = new ArrayList<>(allQuestions);
        Collections.shuffle(shuffledQuestions);

        // Lấy 10 câu đầu tiên từ danh sách đã xáo trộn
        selectedQuestions = shuffledQuestions.subList(0, 10);
    }

    private void loadQuestion() {
        String[] question = selectedQuestions.get(currentQuestionIndex);
        lblQuestion.setText(question[0]);
        btnAnswerA.setText("A. " + question[1]);
        btnAnswerB.setText("B. " + question[2]);
        btnAnswerC.setText("C. " + question[3]);
        btnAnswerD.setText("D. " + question[4]);
    }

    @FXML
    private void handleAnswerA() {
        checkAnswer("A");
    }

    @FXML
    private void handleAnswerB() {
        checkAnswer("B");
    }

    @FXML
    private void handleAnswerC() {
        checkAnswer("C");
    }

    @FXML
    private void handleAnswerD() {
        checkAnswer("D");
    }

    private void checkAnswer(String userAnswer) {
        String correctAnswer = selectedQuestions.get(currentQuestionIndex)[5];
        if (userAnswer.equals(correctAnswer)) {
            lblQuestion.setText("Chính xác! Tiếp tục nào!");
            score++; // Tăng điểm nếu trả lời đúng
        } else {
            lblQuestion.setText("Sai rồi! Đáp án đúng là " + correctAnswer);
        }
        nextQuestion();
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < selectedQuestions.size()) {
            loadQuestion();
        } else {
            lblQuestion.setText("Chúc mừng! Bạn đã hoàn thành trò chơi. Điểm số của bạn: " + score + "/10");
            disableButtons();
        }
    }

    private void disableButtons() {
        btnAnswerA.setDisable(true);
        btnAnswerB.setDisable(true);
        btnAnswerC.setDisable(true);
        btnAnswerD.setDisable(true);
    }
}
