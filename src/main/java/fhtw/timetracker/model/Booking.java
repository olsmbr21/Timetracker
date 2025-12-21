package fhtw.timetracker.model;

/**
 * Stage 03.3: Booking kann auch aus CSV gelesen werden.
 * Format: id;userName;taskId;date;durationMinutes;status
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

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace(";", ",");
    }

    public String toCsvLine() {
        return id + ";" + safe(userName) + ";" + taskId + ";" + safe(date) + ";" + durationMinutes + ";" + safe(status);
    }

    public static Booking fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;

        String[] parts = line.split(";", -1);
        if (parts.length != 6) return null;

        try {
            long id = Long.parseLong(parts[0]);
            String userName = parts[1];
            int taskId = Integer.parseInt(parts[2]);
            String date = parts[3];
            int duration = Integer.parseInt(parts[4]);
            String status = parts[5];

            return new Booking(id, userName, taskId, date, duration, status);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}