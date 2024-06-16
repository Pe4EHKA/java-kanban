package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subTasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.endTime = startTime.plus(duration);
        subTasksIds = new ArrayList<>();
    }

    public List<Integer> getSubTasksIds() {
        return new ArrayList<>(subTasksIds);
    }

    public Integer addTask(Integer subTaskId) {
        if (subTaskId == this.getId()) {
            return null;
        }
        subTasksIds.add(subTaskId);
        return subTaskId;
    }

    public void removeTask(Integer subTaskId) {
        subTasksIds.remove(subTaskId);
    }

    public void removeAllTasks() {
        subTasksIds.clear();
    }

    public boolean isEmpty() {
        return subTasksIds.isEmpty();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Epic epic = (Epic) o;
        return Objects.equals(subTasksIds, epic.getSubTasksIds());
    }
}
