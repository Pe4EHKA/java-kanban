package service;

import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history = new LinkedList<>();

    static final int HISTORY_MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        System.out.println("В историю задач добавлена задача под номером:" + task.getId());
        if (history.size() >= HISTORY_MAX_SIZE) {
            history.removeFirst();
        }
        history.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
