package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class BookingTable {

    public static TableView<Booking> create() {
        TableView<Booking> table = new TableView<>();

        TableColumn<Booking, String> colUser = new TableColumn<>("User");
        colUser.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<Booking, Integer> colTaskId = new TableColumn<>("Task-ID");
        colTaskId.setCellValueFactory(new PropertyValueFactory<>("taskId"));

        TableColumn<Booking, String> colType = new TableColumn<>("Task-Art");
        colType.setCellValueFactory(new PropertyValueFactory<>("taskType"));

        TableColumn<Booking, String> colDate = new TableColumn<>("Datum");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Booking, Integer> colDur = new TableColumn<>("Dauer");
        colDur.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));

        TableColumn<Booking, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Booking, String> colTaskDesc = new TableColumn<>("Task-Beschreibung");
        colTaskDesc.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));

        TableColumn<Booking, String> colDesc = new TableColumn<>("Was wurde gemacht?");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        table.getColumns().addAll(colUser, colTaskId, colType, colDate, colDur, colStatus, colTaskDesc, colDesc);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        return table;
    }
}
