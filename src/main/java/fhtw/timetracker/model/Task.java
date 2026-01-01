package fhtw.timetracker.model;

/**
 * Basisklasse für Tasks.
 * name = UI-Name (z.B. "Support2"), getTypeName() = fachlicher Typ (z.B. "Support").
 */
public class Task {

    private int id;
    private String name;
    private String description;
    private String createdBy;

    public Task(int id, String name, String description, String createdBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    /**
     * Wird von Unterklassen überschrieben (Meeting/Support/Projektarbeit).
     * Basisklasse liefert als Fallback den Namen zurück.
     */
    public String getTypeName() { return name; }

    @Override
    public String toString() {
        return "#" + id + " " + name + " - " + createdBy;
    }
}
