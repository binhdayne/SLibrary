module com.example.login {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.login to javafx.fxml;
    exports com.example.login;

}