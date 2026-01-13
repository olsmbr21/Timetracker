package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import fhtw.timetracker.model.Task;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.List;



public class BookingsTab {

    private TextField txtUser;
    private ObservableList<Task> tasks;
    private TimeTrackerClientService service;
    private ObservableList<Booking> myBookings;

    private List<Booking> source = new ArrayList<>();
    private VBox root;

    private ComboBox<Task> cbTask;
    private DatePicker dpDate;
    private TextField txtDuration;
    private TextField txtDesc;

    private Button btnBook;
    private Button btnLoad;
    private Button btnCancel;

    private DatePicker dpFrom;
    private DatePicker dpTo;
    private Button btnFilter;
    private Button btnReset;

    private TableView<Booking> table;

    public BookingsTab(TextField txtUser, ObservableList<Task> tasks, TimeTrackerClientService service, ObservableList<Booking> myBookings) {
        this.txtUser = txtUser;
        this.tasks = tasks;
        this.service = service;
        this.myBookings = myBookings;
        build();
    }

    public VBox getRoot() {
        return root;
    }

    private void build() {
        cbTask = new ComboBox<>(tasks);
        cbTask.setPromptText("Task wählen");

        dpDate = new DatePicker();

        txtDuration = new TextField();
        txtDuration.setPromptText("Minuten");

        txtDesc = new TextField();
        txtDesc.setPromptText("Was wurde gemacht?");

        btnBook = new Button("Buchen");
        btnLoad = new Button("Meine Buchungen laden");
        btnCancel = new Button("Ausgewählte Buchung stornieren");

        dpFrom = new DatePicker();
        dpTo = new DatePicker();
        btnFilter = new Button("Filtern");
        btnReset = new Button("Filter zurücksetzen");

        table = BookingTable.create();
        table.setItems(myBookings);

        GridPane input = new GridPane();
        input.setHgap(6);
        input.setVgap(6);
        input.add(new Label("Task:"), 0, 0); input.add(cbTask, 1, 0);
        input.add(new Label("Datum:"), 0, 1); input.add(dpDate, 1, 1);
        input.add(new Label("Dauer:"), 0, 2); input.add(txtDuration, 1, 2);
        input.add(new Label("Beschreibung:"), 0, 3); input.add(txtDesc, 1, 3);
        input.add(btnBook, 1, 4);

        HBox loadRow = new HBox(10, btnLoad, btnCancel);
        loadRow.setAlignment(Pos.CENTER);

        HBox filterRow = new HBox(10, new Label("Von:"), dpFrom, new Label("Bis:"), dpTo, btnFilter, btnReset);
        filterRow.setAlignment(Pos.CENTER);

        root = new VBox(10,
                new Label("Neue Buchung:"),
                input,
                new Separator(),
                loadRow,
                new Separator(),
                new Label("Filter (lokal):"),
                filterRow,
                new Label("Meine Buchungen:"),
                table
        );
        root.setPadding(new Insets(10));
    }
}