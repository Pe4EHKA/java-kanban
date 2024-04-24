package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, description);
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

    public boolean isEmpty() {
        return subTasksIds.isEmpty();
    }
}
