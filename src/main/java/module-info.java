module com.example.timetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports fhtw.timetracker.client;

    opens fhtw.timetracker.client to javafx.fxml;

    opens fhtw.timetracker.model to javafx.base, javafx.fxml;
}
