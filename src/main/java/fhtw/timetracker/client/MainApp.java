package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import fhtw.timetracker.model.Task;
import fhtw.timetracker.server.TimeTrackerServer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
public class MainApp extends Application {

    private ObservableList<Task> tasks = FXCollections.observableArrayList();
    private ObservableList<Booking> myBookings = FXCollections.observableArrayList();
    private ObservableList<Booking> allBookings = FXCollections.observableArrayList();
    private TimeTrackerClientService service =
            new TimeTrackerClientService("localhost", TimeTrackerServer.PORT);
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) {
        TextField txtUser = new TextField();
        txtUser.setPromptText("Benutzername");

        HBox top = new HBox(8, new Label("User:"), txtUser);
        top.setPadding(new Insets(8));

        TabPane tabs = new TabPane();

        Tab t1 = new Tab("Tasks", new TasksTab(txtUser, tasks).getRoot());
        t1.setClosable(false);

        Tab t2 = new Tab("Buchungen", new BookingsTab(txtUser, tasks, service, myBookings).getRoot());
        t2.setClosable(false);

        Tab t3 = new Tab("Admin(Ansicht)", new AdminTab(service, allBookings).getRoot());
        t3.setClosable(false);

        tabs.getTabs().addAll(t1, t2, t3);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(tabs);

        stage.setTitle("TimeTracker");
        stage.setScene(new Scene(root, 900, 600));
        stage.show();
    }
}
