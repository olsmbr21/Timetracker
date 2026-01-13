package fhtw.timetracker.client;

import fhtw.timetracker.model.Task;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TasksTab {

    private VBox root;

    public TasksTab(TextField txtUser, ObservableList<Task> tasks) {
        root = new VBox();
    }

    public VBox getRoot() {
        return root;
    }
}
