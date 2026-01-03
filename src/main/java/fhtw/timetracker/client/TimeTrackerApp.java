package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import fhtw.timetracker.model.MeetingTask;
import fhtw.timetracker.model.ProjectTask;
import fhtw.timetracker.model.SupportTask;
import fhtw.timetracker.model.Task;
import fhtw.timetracker.server.TimeTrackerServer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX-Client (GUI) für den TimeTracker.
 * Zuständigkeiten:
 * - UI aufbauen (Tabs: Tasks / Buchungen / Admin)
 * - Eingaben validieren (User, Task, Datum, Dauer)
 * - Netzwerkzugriffe asynchron ausführen (keine Sockets im JavaFX-GUI-Thread)
 * - Tabellen/Listen über ObservableLists automatisch aktualisieren
 */

public class TimeTrackerApp extends Application {

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();

    private final ObservableList<Booking> myBookings = FXCollections.observableArrayList();
    private final ObservableList<Booking> myBookingsSource = FXCollections.observableArrayList();

    private final ObservableList<Booking> allBookings = FXCollections.observableArrayList();

    private final TimeTrackerClientService service =
            new TimeTrackerClientService("127.0.0.1", TimeTrackerServer.PORT);

    private int nextTaskId = 1;

    @Override
    public void start(Stage stage) {

        Label title = new Label("TimeTracker");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField txtUser = new TextField();
        txtUser.setPromptText("Benutzername");

        HBox userBar = new HBox(10, new Label("User:"), txtUser);
        userBar.setAlignment(Pos.CENTER_LEFT);

        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Tasks", createTasksPane(txtUser)));
        tabs.getTabs().add(new Tab("Buchungen", createBookingsPane(txtUser)));
        tabs.getTabs().add(new Tab("Admin", createAdminPane()));
        tabs.getTabs().forEach(t -> t.setClosable(false));

        VBox root = new VBox(12, title, userBar, tabs);
        root.setPadding(new Insets(12));

        stage.setTitle("TimeTracker");
        stage.setScene(new Scene(root, 1200, 700));
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
            if (type == null) type = "Support";

            Task t;
            int id = nextTaskId++;

            if ("Support".equals(type)) {
                t = new SupportTask(id, "Support" + id, desc, createdBy);
            } else if ("Meeting".equals(type)) {
                t = new MeetingTask(id, "Meeting" + id, desc, createdBy);
            } else {
                t = new ProjectTask(id, "Projektarbeit" + id, desc, createdBy);
            }

            tasks.add(t);
            txtDesc.clear();

            lbl.setText("OK: Task erstellt (" + t.getTypeName() + ")");
        });

        HBox form = new HBox(10,
                new Label("Typ:"), cmbType,
                new Label("Beschreibung:"), txtDesc,
                btnAdd
        );
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

        DatePicker dpFrom = new DatePicker();
        DatePicker dpTo = new DatePicker();

        Button btnFilter = new Button("Filtern");
        Button btnReset = new Button("Reset");

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

        TableColumn<Booking, String> colDate = new TableColumn<>("Datum");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Booking, Integer> colDur = new TableColumn<>("Min");
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
            if (user.isEmpty()) {
                lbl.setText("User fehlt.");
                return;
            }

            Task task = cmbTask.getValue();
            if (task == null) {
                lbl.setText("Task auswählen.");
                return;
            }

            LocalDate date = dpDate.getValue();
            if (date == null) {
                lbl.setText("Datum auswählen.");
                return;
            }

            int minutes;
            try {
                minutes = Integer.parseInt(txtDuration.getText().trim());
            } catch (NumberFormatException ex) {
                lbl.setText("Dauer muss eine Zahl sein.");
                return;
            }

            if (minutes <= 0) {
                lbl.setText("Dauer muss > 0 sein.");
                return;
            }

            Booking b = new Booking(
                    System.currentTimeMillis(),
                    user,
                    task.getId(),
                    date.toString(),
                    minutes,
                    Booking.STATUS_ACTIVE,
                    txtDesc.getText().trim(),
                    task.getDescription(),
                    task.getTypeName()
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
            }, "client-create-booking").start();
        });

        btnLoad.setOnAction(e -> {

            String user = txtUser.getText().trim();
            if (user.isEmpty()) {
                lbl.setText("User fehlt.");
                return;
            }

            new Thread(() -> {
                try {
                    List<Booking> list = service.loadBookingsForUser(user);
                    Platform.runLater(() -> {
                        myBookingsSource.setAll(list);
                        myBookings.setAll(list);
                        lbl.setText("OK: " + list.size() + " Buchungen geladen.");
                    });
                } catch (IOException ex) {
                    Platform.runLater(() -> lbl.setText("Fehler: " + ex.getMessage()));
                }
            }, "client-load-bookings").start();
        });

        btnCancel.setOnAction(e -> {

            String user = txtUser.getText().trim();
            if (user.isEmpty()) {
                lbl.setText("User fehlt.");
                return;
            }

            Booking selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                lbl.setText("Bitte eine Buchung auswählen.");
                return;
            }

            new Thread(() -> {
                try {
                    service.cancelBooking(selected.getId(), user);

                    List<Booking> list = service.loadBookingsForUser(user);
                    Platform.runLater(() -> {
                        myBookingsSource.setAll(list);
                        myBookings.setAll(list);
                        lbl.setText("OK: Buchung storniert.");
                    });

                } catch (IOException ex) {
                    Platform.runLater(() -> lbl.setText("Fehler: " + ex.getMessage()));
                }
            }, "client-cancel-booking").start();
        });

        btnFilter.setOnAction(e -> {
            LocalDate from = dpFrom.getValue();
            LocalDate to = dpTo.getValue();

            List<Booking> filtered = new ArrayList<>();
            for (Booking b : myBookingsSource) {
                boolean ok = true;
                if (from != null && b.getDate().compareTo(from.toString()) < 0) ok = false;
                if (to != null && b.getDate().compareTo(to.toString()) > 0) ok = false;

                if (ok) filtered.add(b);
            }
            myBookings.setAll(filtered);
            lbl.setText("OK: Filter angewendet.");
        });

        btnReset.setOnAction(e -> {
            myBookings.setAll(myBookingsSource);
            lbl.setText("OK: Filter zurückgesetzt.");
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        int r = 0;
        form.add(new Label("Task:"), 0, r);
        form.add(cmbTask, 1, r);
        form.add(new Label("Datum:"), 2, r);
        form.add(dpDate, 3, r);

        r++;
        form.add(new Label("Minuten:"), 0, r);
        form.add(txtDuration, 1, r);
        form.add(new Label("Was wurde gemacht?:"), 2, r);
        form.add(txtDesc, 3, r);

        r++;
        form.add(btnCreate, 0, r);
        form.add(btnCancel, 1, r);
        form.add(btnLoad, 2, r);

        r++;
        form.add(new Label("Von:"), 0, r);
        form.add(dpFrom, 1, r);
        form.add(new Label("Bis:"), 2, r);
        form.add(dpTo, 3, r);

        r++;
        form.add(btnFilter, 0, r);
        form.add(btnReset, 1, r);

        VBox box = new VBox(10, form, table, lbl);
        box.setPadding(new Insets(10));
        return box;
    }

    private Pane createAdminPane() {

        Label lbl = new Label();

        TableView<Booking> table = new TableView<>(allBookings);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Booking, String> colUser = new TableColumn<>("User");
        colUser.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<Booking, Integer> colTaskId = new TableColumn<>("TaskId");
        colTaskId.setCellValueFactory(new PropertyValueFactory<>("taskId"));

        TableColumn<Booking, String> colType = new TableColumn<>("Task-Art");
        colType.setCellValueFactory(new PropertyValueFactory<>("taskType"));

        TableColumn<Booking, String> colDate = new TableColumn<>("Datum");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Booking, Integer> colDur = new TableColumn<>("Min");
        colDur.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

        TableColumn<Booking, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(colId, colUser, colTaskId, colType, colDate, colDur, colStatus);

        Button btnLoadAll = new Button("Alle Buchungen laden");
        btnLoadAll.setOnAction(e -> new Thread(() -> {
            try {
                List<Booking> list = service.loadAllBookings();
                Platform.runLater(() -> {
                    allBookings.setAll(list);
                    lbl.setText("OK: " + list.size() + " Buchungen geladen.");
                });
            } catch (IOException ex) {
                Platform.runLater(() -> lbl.setText("Fehler: " + ex.getMessage()));
            }
        }, "client-load-all").start());

        VBox box = new VBox(10, btnLoadAll, table, lbl);
        box.setPadding(new Insets(10));
        return box;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

