package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import java.io.IOException;

public class AdminTab {

    private TimeTrackerClientService service;
    private ObservableList<Booking> allBookings;

    private List<Booking> source = new ArrayList<>();
    private VBox root;

    private TableView<Booking> table;

    private Button btnLoadAll;
    private TextField txtUserFilter;
    private TextField txtTaskIdFilter;
    private Button btnApply;
    private Button btnReset;

    public AdminTab(TimeTrackerClientService service, ObservableList<Booking> allBookings) {
        this.service = service;
        this.allBookings = allBookings;
        build();
    }

    public VBox getRoot() {
        return root;
    }

    private void build() {
        table = BookingTable.create();
        table.setItems(allBookings);

        btnLoadAll = new Button("Alle Buchungen laden");

        txtUserFilter = new TextField();
        txtUserFilter.setPromptText("User filter (optional)");

        txtTaskIdFilter = new TextField();
        txtTaskIdFilter.setPromptText("Task-ID (optional)");

        btnApply = new Button("Filter anwenden");
        btnReset = new Button("Filter zurücksetzen");

        HBox controls = new HBox(10, btnLoadAll, txtUserFilter, txtTaskIdFilter, btnApply, btnReset);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(10));

        root = new VBox(10, controls, new Label("Alle Buchungen:"), table);
        root.setPadding(new Insets(10));

        btnLoadAll.setOnAction(e -> {
            Thread thread = new Thread(() -> {
                try {
                    List<Booking> list = service.loadAllBookings();
                    Platform.runLater(() -> setList(list));
                } catch (IOException ex) {
                    Platform.runLater(() -> UiPopups.error("Beim Laden aller Buchungen ist ein Fehler aufgetreten."));
                }
            }, "client-load-all-bookings");
            thread.setDaemon(true);
            thread.start();
        });

        btnApply.setOnAction(e -> {
            String user = txtUserFilter.getText().trim();
            Integer taskId = null;

            try {
                String raw = txtTaskIdFilter.getText().trim();
                if (!raw.isEmpty()) taskId = Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                UiPopups.warn("Task-ID muss ein Zahl sein");
            }

            List<Booking> filtered = new ArrayList<>();
            for (Booking b : source) {
                boolean ok = true;
                if (!user.isEmpty()) ok = user.equals(b.getUserName());
                if (ok && taskId != null) ok = (b.getTaskId() == taskId);
                if (ok) filtered.add(b);
            }
            allBookings.setAll(filtered);
        });

        btnReset.setOnAction(e -> allBookings.setAll(source));
    }

    private void setList(List<Booking> list) {
        source.clear();
        source.addAll(list);
        allBookings.setAll(list);
    }

}