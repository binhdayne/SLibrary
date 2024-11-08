module com.example.login {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires javafx.media;

    opens com.example.login to javafx.fxml;
    exports com.example.login;

}