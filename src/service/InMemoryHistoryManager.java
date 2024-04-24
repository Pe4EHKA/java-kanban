package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        System.out.println("В историю задач добавлена задача под номером:" + task.getId());
        if (history.size() > 9) {
            history.removeFirst();
        }
        if (task instanceof Epic) {
            Epic epicSaving = new Epic(task.getName(), task.getDescription());
            epicSaving.setId(task.getId());
            epicSaving.setStatus(task.getStatus());
            List<Integer> subTasksIds = ((Epic) task).getSubTasksIds();
            for (Integer subTasksId : subTasksIds) {
                epicSaving.addTask(subTasksId);
            }
            history.add(epicSaving);
        } else if (task instanceof SubTask) {
            SubTask subTaskSaving = new SubTask(task.getName(), task.getDescription(), ((SubTask) task).getEpicId());
            subTaskSaving.setId(task.getId());
            subTaskSaving.setStatus(task.getStatus());
            history.add(subTaskSaving);
        } else {
            Task taskSaving = new Task(task.getName(), task.getDescription());
            taskSaving.setId(task.getId());
            taskSaving.setStatus(task.getStatus());
            history.add(taskSaving);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
