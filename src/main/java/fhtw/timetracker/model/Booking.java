package fhtw.timetracker.model;

/**
 * Stage 03.1: Booking-Datenmodell (ohne CSV-Logik).
 */
public class Booking {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CANCELED = "CANCELED";

    private long id;
    private String userName;
    private int taskId;
    private String date; // yyyy-MM-dd
    private int durationMinutes;
    private String status;

    public Booking(long id, String userName, int taskId, String date, int durationMinutes, String status) {
        this.id = id;
        this.userName = userName;
        this.taskId = taskId;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }

    public long getId() { return id; }
    public String getUserName() { return userName; }
    public int getTaskId() { return taskId; }
    public String getDate() { return date; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
