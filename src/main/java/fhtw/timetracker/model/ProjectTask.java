package fhtw.timetracker.model;

/**
 * Task-Typ: Projektarbeit .
 */
public class ProjectTask extends Task {

    public ProjectTask(int id, String description, String createdBy) {
        super(id, "Projektarbeit", description, createdBy);
    }

    public ProjectTask(int id, String numberedName, String description, String createdBy) {
        super(id, numberedName, description, createdBy);
    }

    @Override
    public String getTypeName() { return "Projektarbeit"; }
}
