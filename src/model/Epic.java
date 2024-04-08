package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    public void removeTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public void updateStatus() {  // Расчитывание статуса Эпика
        if (subTasks.isEmpty() || this.isAllNew()) {
            this.setStatus(Status.NEW);
        } else if (this.isAllDone()) {
            this.setStatus(Status.DONE);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }

    public boolean isEmpty() {
        return subTasks.isEmpty();
    }

    public boolean isAllDone() {
        for (SubTask subTask : subTasks) {
            if (!Status.DONE.equals(subTask.getStatus())) {
                return false;
            }
        }
        return true;
    }

    public boolean isAllNew() {
        for (SubTask subTask : subTasks) {
            if (!Status.NEW.equals(subTask.getStatus())) {
                return false;
            }
        }
        return true;
    }


}
