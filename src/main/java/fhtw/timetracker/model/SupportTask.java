package fhtw.timetracker.model;


public class SupportTask extends Task {

    public SupportTask(int id, String numberedName, String description, String createdBy) {
        super(id, numberedName, description, createdBy);
    }

    @Override
    public String getTypeName() { return "Support"; }
}
