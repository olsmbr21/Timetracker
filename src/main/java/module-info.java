module com.example.timetracker {
    requires javafx.controls;
    requires javafx.fxml;

    opens fhtw.timetracker.model to javafx.base;

}