package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epic;

    public SubTask(String name, String description, int epic, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epic = epic;
    }

    public Integer setEpicId(int epic) {
        if (epic == this.getId()) {
            return null;
        }
        this.epic = epic;
        return this.epic;
    }

    @Override
    public Integer getEpicId() {
        return epic;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
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
