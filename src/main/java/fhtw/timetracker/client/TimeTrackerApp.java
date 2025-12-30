package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import fhtw.timetracker.model.MeetingTask;
import fhtw.timetracker.model.ProjectTask;
import fhtw.timetracker.model.SupportTask;
import fhtw.timetracker.model.Task;
import fhtw.timetracker.server.TimeTrackerServer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TimeTrackerApp extends Application {

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final ObservableList<Booking> myBookings = FXCollections.observableArrayList();

    private final TimeTrackerClientService service =
            new TimeTrackerClientService("127.0.0.1", TimeTrackerServer.PORT);

    private int nextTaskId = 1;

    @Override
    public void start(Stage stage) {
        TextField txtUser = new TextField();
        txtUser.setPromptText("Benutzername");

        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Tasks", createTasksPane(txtUser)));
        tabs.getTabs().add(new Tab("Bookings", createBookingsPane(txtUser)));
        tabs.getTabs().forEach(t -> t.setClosable(false));

        VBox root = new VBox(10, new Label("User:"), txtUser, tabs);
        root.setPadding(new Insets(12));

        stage.setTitle("TimeTracker (Stage 06.4)");
        stage.setScene(new Scene(root, 1000, 600));
        stage.show();
    }

    private Pane createTasksPane(TextField txtUser) {
        ListView<Task> list = new ListView<>(tasks);

        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("Support", "Meeting", "Projektarbeit");
        cmbType.getSelectionModel().selectFirst();

        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Task Beschreibung");

        Button btnAdd = new Button("Task hinzufügen");
        Label lbl = new Label();

        btnAdd.setOnAction(e -> {
            String createdBy = txtUser.getText().trim();
            if (createdBy.isEmpty()) { lbl.setText("Bitte zuerst User eingeben."); return; }

            String desc = txtDesc.getText().trim();
            String type = cmbType.getValue();

            int id = nextTaskId++;
            Task t;
            if ("Meeting".equals(type)) t = new MeetingTask(id, "Meeting" + id, desc, createdBy);
            else if ("Projektarbeit".equals(type)) t = new ProjectTask(id, "Projektarbeit" + id, desc, createdBy);
            else t = new SupportTask(id, "Support" + id, desc, createdBy);

            tasks.add(t);
            txtDesc.clear();
            lbl.setText("OK: Task erstellt");
        });

        VBox box = new VBox(10,
                new HBox(10, new Label("Typ:"), cmbType, new Label("Beschreibung:"), txtDesc, btnAdd),
                list,
                lbl
        );
        box.setPadding(new Insets(10));
        return box;
    }

    private Pane createBookingsPane(TextField txtUser) {
        ComboBox<Task> cmbTask = new ComboBox<>(tasks);
        cmbTask.setPrefWidth(220);

        DatePicker dpDate = new DatePicker(LocalDate.now());
        TextField txtMinutes = new TextField();
        txtMinutes.setPromptText("Minuten");

        Button btnSend = new Button("Buchung senden");
        Button btnLoad = new Button("Meine Buchungen laden");
        Label lbl = new Label();

        TableView<Booking> table = new TableView<>(myBookings);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Booking, String> colUser = new TableColumn<>("User");
        colUser.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<Booking, Integer> colTaskId = new TableColumn<>("TaskId");
        colTaskId.setCellValueFactory(new PropertyValueFactory<>("taskId"));

        TableColumn<Booking, String> colDate = new TableColumn<>("Datum");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Booking, Integer> colMin = new TableColumn<>("Min");
        colMin.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

        TableColumn<Booking, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colId, colUser, colTaskId, colDate, colMin, colStatus);

        btnSend.setOnAction(e -> {
            String user = txtUser.getText().trim();
            Task task = cmbTask.getValue();
            LocalDate date = dpDate.getValue();

            int minutes;
            try { minutes = Integer.parseInt(txtMinutes.getText().trim()); }
            catch (NumberFormatException ex) { lbl.setText("Minuten muss Zahl sein."); return; }

            if (user.isEmpty()) { lbl.setText("User fehlt."); return; }
            if (task == null) { lbl.setText("Task auswählen."); return; }
            if (date == null) { lbl.setText("Datum fehlt."); return; }
            if (minutes <= 0) { lbl.setText("Minuten > 0."); return; }

            Booking b = new Booking(System.currentTimeMillis(), user, task.getId(), date.toString(), minutes, Booking.STATUS_ACTIVE);

            try {
                service.sendBooking(b); // noch im UI-Thread (wird in 06.5 verbessert)
                lbl.setText("OK: gesendet");
            } catch (IOException ex) {
                lbl.setText("Fehler: " + ex.getMessage());
            }
        });

        btnLoad.setOnAction(e -> {
            String user = txtUser.getText().trim();
            if (user.isEmpty()) { lbl.setText("User fehlt."); return; }

            try {
                List<Booking> list = service.loadBookingsForUser(user); // noch im UI-Thread
                myBookings.setAll(list);
                lbl.setText("OK: geladen (" + list.size() + ")");
            } catch (IOException ex) {
                lbl.setText("Fehler: " + ex.getMessage());
            }
        });

        VBox box = new VBox(10,
                new HBox(10, new Label("Task:"), cmbTask, new Label("Datum:"), dpDate, new Label("Min:"), txtMinutes, btnSend, btnLoad),
                table,
                lbl
        );
        box.setPadding(new Insets(10));
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

