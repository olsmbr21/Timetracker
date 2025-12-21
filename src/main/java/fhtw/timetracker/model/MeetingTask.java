package fhtw.timetracker.model;

/**
 * Task-Typ: Meeting
 */
public class MeetingTask extends Task {

    public MeetingTask(int id, String description, String createdBy) {
        super(id, "Meeting", description, createdBy);
    }

    public MeetingTask(int id, String numberedName, String description, String createdBy) {
        super(id, numberedName, description, createdBy);
    }

    @Override
    public String getTypeName() { return "Meeting"; }
}

