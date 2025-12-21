package fhtw.timetracker.model;

/**
 * Task-Typ: Support.
 */
public class SupportTask extends Task {

    public SupportTask(int id, String description, String createdBy) {
        super(id, "Support", description, createdBy);
    }

    public SupportTask(int id, String numberedName, String description, String createdBy) {
        super(id, numberedName, description, createdBy);
    }

    @Override
    public String getTypeName() { return "Support"; }
}
