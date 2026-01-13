package fhtw.timetracker.client;

import fhtw.timetracker.model.MeetingTask;
import fhtw.timetracker.model.ProjectTask;
import fhtw.timetracker.model.SupportTask;
import fhtw.timetracker.model.Task;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


public class TasksTab {

    private TextField txtUser;
    private ObservableList<Task> tasks;
    private VBox root;

    private int nextTaskId = 1;

    private ComboBox<String> cbType;
    private TextArea txtDesc;
    private Button btnAdd;
    private ListView<Task> listView;
    public TasksTab(TextField txtUser, ObservableList<Task> tasks) {
        this.txtUser = txtUser;
        this.tasks = tasks;
        build();
    }

    public VBox getRoot() {
        return root;
    }

    private void build() {
        cbType = new ComboBox<>();
        cbType.getItems().addAll("Projektarbeit", "Support", "Meeting");
        cbType.setPromptText("Task-Typ wählen");

        txtDesc = new TextArea();
        txtDesc.setPromptText("Beschreibung");
        txtDesc.setPrefRowCount(3);

        btnAdd = new Button("Task hinzufügen");

        listView = new ListView<>(tasks);


        btnAdd.setOnAction(e -> {
            String user = txtUser.getText().trim();
            String type = cbType.getValue();
            String desc = txtDesc.getText().trim();

            if (user.isEmpty()) { UiPopups.warn("Bitte zuerst einen Benutzernamen eingeben."); return; }
            if (type == null || type.isBlank()) { UiPopups.warn("Bitte einen Task-Typ auswählen."); return; }

            int id = nextTaskId++;

            int number = 1;
            for (Task t : tasks) {
                if (user.equals(t.getCreatedBy()) && type.equals(t.getTypeName())) number++;
            }

            String name = type + number;

            Task task;
            if ("Projektarbeit".equals(type)) task = new ProjectTask(id, name, desc, user);
            else if ("Support".equals(type)) task = new SupportTask(id, name, desc, user);
            else task = new MeetingTask(id, name, desc, user);

            tasks.add(task);

            cbType.getSelectionModel().clearSelection();
            txtDesc.clear();
        });

        root = new VBox(10,
                new Label("Neuer Task:"),
                cbType,
                txtDesc,
                btnAdd,
                new Label("Tasks:"),
                listView
        );
        root.setPadding(new Insets(10));
    }
}