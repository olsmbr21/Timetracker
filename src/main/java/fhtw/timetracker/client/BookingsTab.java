package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import fhtw.timetracker.model.Task;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class BookingsTab {

    private VBox root;

    public BookingsTab(TextField txtUser, ObservableList<Task> tasks, TimeTrackerClientService service, ObservableList<Booking> myBookings) {
        root = new VBox();
    }

    public VBox getRoot() {
        return root;
    }
}