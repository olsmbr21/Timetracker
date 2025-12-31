package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import fhtw.timetracker.model.MeetingTask;
import fhtw.timetracker.model.ProjectTask;
import fhtw.timetracker.model.SupportTask;
import fhtw.timetracker.model.Task;
import javafx.application.Application;
import javafx.application.Platform;
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

/**
 * Stage 15: GUI kann jetzt eine eigene Buchung stornieren (Server prüft bookingId + username).
 */
public class TimeTrackerApp extends Application {

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final ObservableList<Booking> myBookings = FXCollections.observableArrayList();

    private final TimeTrackerClientService service = new TimeTrackerClientService("127.0.0.1", 5000);

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

        stage.setTitle("TimeTracker (Stage 15)");
        stage.setScene(new Scene(root, 980, 600));
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
            if (createdBy.isEmpty()) {
                lbl.setText("Bitte zuerst User eingeben.");
                return;
            }
            String desc = txtDesc.getText().trim();
            String type = cmbType.getValue();

            Task t;
            int id = nextTaskId++;
            if ("Support".equals(type)) t = new SupportTask(id, "Support" + id, desc, createdBy);
            else if ("Meeting".equals(type)) t = new MeetingTask(id, "Meeting" + id, desc, createdBy);
            else t = new ProjectTask(id, "Projektarbeit" + id, desc, createdBy);

            tasks.add(t);
            txtDesc.clear();
            lbl.setText("OK: Task #" + t.getId() + " (" + t.getTypeName() + ")");
        });

        HBox form = new HBox(10, new Label("Typ:"), cmbType, new Label("Beschreibung:"), txtDesc, btnAdd);
        form.setPadding(new Insets(10));

        VBox box = new VBox(10, form, list, lbl);
        box.setPadding(new Insets(10));
        return box;
    }

    private Pane createBookingsPane(TextField txtUser) {
        ComboBox<Task> cmbTask = new ComboBox<>(tasks);
        cmbTask.setPrefWidth(240);

        DatePicker dpDate = new DatePicker(LocalDate.now());
        TextField txtDuration = new TextField();
        txtDuration.setPromptText("Minuten");

        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Was wurde gemacht?");

        Button btnCreate = new Button("Buchung senden");
        Button btnCancel = new Button("Buchung stornieren");
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

        TableColumn<Booking, String> colType = new TableColumn<>("Task-Art");
        colType.setCellValueFactory(new PropertyValueFactory<>("taskType"));

        TableColumn<Booking, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Booking, Integer> colDur = new TableColumn<>("Minutes");
        colDur.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

        TableColumn<Booking, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Booking, String> colTaskDesc = new TableColumn<>("Task-Beschreibung");
        colTaskDesc.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));

        TableColumn<Booking, String> colDesc = new TableColumn<>("Was wurde gemacht?");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(colId, colUser, colTaskId, colType, colDate, colDur, colStatus, colTaskDesc, colDesc);

        btnCreate.setOnAction(e -> {
            String user = txtUser.getText().trim();
            Task t = cmbTask.getValue();
            LocalDate date = dpDate.getValue();

            int minutes;
            try {
                minutes = Integer.parseInt(txtDuration.getText().trim());
            } catch (NumberFormatException ex) {
                lbl.setText("Dauer muss eine Zahl sein.");
                return;
            }

            if (user.isEmpty()) { lbl.setText("User fehlt."); return; }
            if (t == null) { lbl.setText("Task auswählen."); return; }
            if (date == null) { lbl.setText("Datum auswählen."); return; }
            if (minutes <= 0) { lbl.setText("Dauer > 0."); return; }

            Booking b = new Booking(
                    System.currentTimeMillis(),
                    user,
                    t.getId(),
                    date.toString(),
                    minutes,
                    Booking.STATUS_ACTIVE,
                    txtDesc.getText().trim(),
                    t.getDescription(),
                    t.getTypeName()
            );

            new Thread(() -> {
                try {
                    service.sendBooking(b);
                    Platform.runLater(() -> {
                        lbl.setText("OK: Buchung gesendet.");
                        txtDuration.clear();
                        txtDesc.clear();
                    });
                } catch (IOException ex) {
                    Platform.runLater(() -> lbl.setText("Fehler: " + ex.getMessage()));
                }
            }, "client-send-booking").start();
        });

        btnCancel.setOnAction(e -> {
            Booking sel = table.getSelectionModel().getSelectedItem();
            String user = txtUser.getText().trim();

            if (sel == null) { lbl.setText("Bitte eine Buchung auswählen."); return; }
            if (user.isEmpty()) { lbl.setText("User fehlt."); return; }

            new Thread(() -> {
                try {
                    service.cancelBooking(sel.getId(), user);
                    var list = service.loadBookingsForUser(user);
                    Platform.runLater(() -> {
                        myBookings.setAll(list);
                        lbl.setText("OK: Buchung storniert.");
                    });
                } catch (IOException ex) {
                    Platform.runLater(() -> lbl.setText("Fehler: " + ex.getMessage()));
                }
            }, "client-cancel-booking").start();
        });

        btnLoad.setOnAction(e -> {
            String user = txtUser.getText().trim();
            if (user.isEmpty()) { lbl.setText("User fehlt."); return; }

            new Thread(() -> {
                try {
                    var list = service.loadBookingsForUser(user);
                    Platform.runLater(() -> {
                        myBookings.setAll(list);
                        lbl.setText("OK: " + list.size() + " Buchungen geladen.");
                    });
                } catch (IOException ex) {
                    Platform.runLater(() -> lbl.setText("Fehler: " + ex.getMessage()));
                }
            }, "client-load-bookings").start();
        });

        HBox form = new HBox(10,
                new Label("Task:"), cmbTask,
                new Label("Datum:"), dpDate,
                new Label("Min:"), txtDuration,
                new Label("Desc:"), txtDesc,
                btnCreate, btnCancel, btnLoad
        );
        form.setPadding(new Insets(10));

        VBox box = new VBox(10, form, table, lbl);
        box.setPadding(new Insets(10));
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

