package model;

public class SubTask extends Task {
    private int epic;

    public SubTask(String name, String description, int epic) {
        super(name, description);
        this.epic = epic;
    }

    public Integer setEpicId(int epic) {
        if (epic == this.getId()) {
            return null;
        }
        this.epic = epic;
        return this.epic;
    }

    public int getEpicId() {
        return epic;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        SubTask subTask = (SubTask) o;
        return epic == subTask.getEpicId();
    }
}
