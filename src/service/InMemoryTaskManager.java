package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int seq = 0;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    private int generateId() {
        return ++seq;
    }  // Генерация идентификатора.

    @Override
    public List<Task> getTasks() {  // Получение списка всех задач.
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteAllTasks() {  // Удаление всех задач.
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        for (Integer subTaskId : subTasks.keySet()) {
            historyManager.remove(subTaskId);
        }
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (Integer subTaskId : subTasks.keySet()) {
            historyManager.remove(subTaskId);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasksIds().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Task getTask(int id) {  // Получение по идентификатору.
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }


    @Override
    public Task createTask(Task task) {  // Создание. Сам объект должен передаваться в качестве параметра.
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        Epic savedEpic = epics.get(subTask.getEpicId());
        savedEpic.addTask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(savedEpic);
        return subTask;
    }

    @Override
    public void updateTask(Task task) {  // Обновление.
        // Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (tasks.get(task.getId()) == null) {
            throw new IllegalStateException("Task with id " + task.getId() + " does not exist");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            throw new IllegalStateException("Epic with id " + epic.getId() + " does not exist");
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        Integer epicId = subTask.getEpicId();
        Epic savedEpic = epics.get(epicId);
        if (savedEpic == null) {
            throw new IllegalStateException("Epic id " + epicId + " of subtask with id " +
                    subTask.getId() + " does not exist");
        }
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(savedEpic);
    }

    @Override
    public void deleteByIdTask(int id) {  // Удаление по идентификатору.
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteByIdEpic(int id) {
        Epic savedEpic = epics.remove(id);
        if (savedEpic == null) {
            throw new IllegalStateException("Epic with id " + id + " does not exist");
        }
        historyManager.remove(savedEpic.getId());
        for (Integer subTasksIds : savedEpic.getSubTasksIds()) {
            subTasks.remove(subTasksIds);
            historyManager.remove(subTasksIds);
        }
    }

    @Override
    public void deleteByIdSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask == null) {
            throw new IllegalStateException("Subtask with id " + id + " does not exist");
        }
        int subTaskId = subTask.getId();
        historyManager.remove(subTaskId);
        Epic savedEpic = epics.get(subTask.getEpicId());
        savedEpic.removeTask(subTaskId);
        updateEpicStatus(savedEpic);
    }

    @Override
    public List<SubTask> getAllSubStacksByEpicId(int id) {  // Получение списка всех подзадач определённого эпика.
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) {
            throw new IllegalStateException("Epic with id " + id + " does not exist");
        }
        ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
        for (Integer subTaskId : savedEpic.getSubTasksIds()) {
            subTaskArrayList.add(subTasks.get(subTaskId));
        }
        return subTaskArrayList;
    }

    @Override
    public void updateEpicStatus(Epic epic) {  // Расчитывание статуса Эпика
        if (epic.isEmpty() || isEpicAllNew(epic)) {
            epic.setStatus(Status.NEW);
        } else if (isEpicAllDone(epic)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private boolean isEpicAllDone(Epic epic) {
        List<Integer> subTasksIdsOfEpic = epic.getSubTasksIds();
        for (Integer subTaskId : subTasksIdsOfEpic) {
            if (!Status.DONE.equals(subTasks.get(subTaskId).getStatus())) {
                return false;
            }
        }
        return true;
    }

    private boolean isEpicAllNew(Epic epic) {
        List<Integer> subTasksIdsOfEpic = epic.getSubTasksIds();
        for (Integer subTaskId : subTasksIdsOfEpic) {
            if (!Status.NEW.equals(subTasks.get(subTaskId).getStatus())) {
                return false;
            }
        }
        return true;
    }
}
