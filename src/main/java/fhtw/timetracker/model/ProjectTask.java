package fhtw.timetracker.model;


public class ProjectTask extends Task {

    public ProjectTask(int id, String numberedName, String description, String createdBy) {
        super(id, numberedName, description, createdBy);
    }

    @Override
    public String getTypeName() { return "Projektarbeit"; }
}
