package model;

public class SubTask extends Task {
    private int epic;

    public SubTask(String name, String description, int epic) {
        super(name, description);
        this.epic = epic;
    }

    public void setEpicId(int epic) {
        this.epic = epic;
    }

    public int getEpicId() {
        return epic;
    }
}
