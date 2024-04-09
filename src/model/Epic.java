package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, description);
        subTasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void addTask(Integer subTaskId) {
        subTasksIds.add(subTaskId);
    }

    public void removeTask(Integer subTaskId) {
        subTasksIds.remove(subTaskId);
    }

    public boolean isEmpty() {
        return subTasksIds.isEmpty();
    }
}
