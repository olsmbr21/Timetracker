package fhtw.timetracker.client;

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
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class TimeTrackerApp extends Application {

    private final ObservableList<Task> tasks = FXCollections.observableArrayList();
    private final TimeTrackerClientService service =
            new TimeTrackerClientService("127.0.0.1", TimeTrackerServer.PORT);

    private int nextTaskId = 1;

    @Override
    public void start(Stage stage) {
        TextField txtUser = new TextField();
        txtUser.setPromptText("Benutzername");

        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Tasks", createTasksPane(txtUser)));
        tabs.getTabs().add(new Tab("Bookings", new Label("Kommt in Stage 06.4")));
        tabs.getTabs().forEach(t -> t.setClosable(false));

        VBox root = new VBox(10, new Label("User:"), txtUser, tabs);
        root.setPadding(new Insets(12));

        stage.setTitle("TimeTracker (Stage 06.3)");
        stage.setScene(new Scene(root, 900, 550));
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

    public static void main(String[] args) {
        launch(args);
    }
}