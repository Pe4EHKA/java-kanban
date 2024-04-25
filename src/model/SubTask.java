package model;

import java.util.Objects;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        return this.getId() == subTask.getId() &&
                this.getEpicId() == subTask.getEpicId() &&
                Objects.equals(this.getName(), subTask.getName()) &&
                Objects.equals(this.getDescription(), subTask.getDescription()) &&
                this.getStatus() == subTask.getStatus();
    }
}
