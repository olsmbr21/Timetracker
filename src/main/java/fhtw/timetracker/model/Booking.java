package fhtw.timetracker.model;

public class Booking {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CANCELED = "CANCELED";

    private long id;
    private String userName;
    private int taskId;
    private String date;
    private int durationMinutes;
    private String status;

    private String description;
    private String taskDescription;
    private String taskType;

    public Booking(long id, String userName, int taskId, String date,
                   int durationMinutes, String status,
                   String description, String taskDescription, String taskType) {
        this.id = id;
        this.userName = userName;
        this.taskId = taskId;
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.status = status;
        this.description = description;
        this.taskDescription = taskDescription;
        this.taskType = taskType;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String toCsvLine() {
        return id + ";" + safe(userName) + ";" + taskId + ";" + safe(date) + ";" + durationMinutes + ";" + safe(status) + ";"
                + safe(description) + ";" + safe(taskDescription) + ";" + safe(taskType);
    }

    public static Booking fromCsvLine(String line) {
        if (line == null || line.isBlank()) return null;

        String[] parts = line.split(";", -1);
        if (parts.length != 9) return null;

        try {
            long id = Long.parseLong(parts[0]);
            String userName = parts[1];
            int taskId = Integer.parseInt(parts[2]);
            String date = parts[3];
            int duration = Integer.parseInt(parts[4]);
            String status = parts[5];
            String bookingDesc = parts[6];
            String taskDesc = parts[7];
            String taskType = parts[8];

            return new Booking(id, userName, taskId, date, duration, status, bookingDesc, taskDesc, taskType);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.replace(";", ",");
    }
}

