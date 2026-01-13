package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

public class AdminTab {

    private VBox root;

    public AdminTab(TimeTrackerClientService service, ObservableList<Booking> allBookings) {
        root = new VBox();
    }

    public VBox getRoot() {
        return root;
    }
}