package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int seq = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateId() {
        return ++seq;
    }  // Генерация идентификатора.

    public ArrayList<Task> getTasks() {  // Получение списка всех задач.
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllTasks() {  // Удаление всех задач.
        tasks.clear();
    }

    public void deleteAllEpics() {
        subTasks.clear();
        epics.clear();
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasksIds().clear();
            updateEpicStatus(epic);
        }
    }

    public Task getTask(int id) {  // Получение по идентификатору.
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public Task createTask(Task task) {  // Создание. Сам объект должен передаваться в качестве параметра.
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        Epic savedEpic = epics.get(subTask.getEpicId());
        savedEpic.addTask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(savedEpic);
        return subTask;
    }

    public void updateTask(Task task) {  // Обновление.
        // Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (tasks.get(task.getId()) == null) return;
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) return;
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        epics.put(savedEpic.getId(), savedEpic);
    }

    public void updateSubTask(SubTask subTask) {
        Integer epicId = subTask.getEpicId();
        Epic savedEpic = epics.get(epicId);
        if (savedEpic == null) return;
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(savedEpic);
    }

    public void deleteByIdTask(int id) {  // Удаление по идентификатору.
        tasks.remove(id);
    }

    public void deleteByIdEpic(int id) {
        Epic savedEpic = epics.remove(id);
        if (savedEpic == null) return;
        for (Integer subTasksIds : savedEpic.getSubTasksIds()) {
            subTasks.remove(subTasksIds);
        }
    }

    public void deleteByIdSubTask(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask == null) return;
        Epic savedEpic = epics.get(subTask.getEpicId());
        savedEpic.removeTask(subTask.getId());
        updateEpicStatus(savedEpic);
    }

    public ArrayList<SubTask> getAllSubStacksByEpicId(int id) {  // Получение списка всех подзадач определённого эпика.
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) return null;
        ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
        for (Integer subTaskId : savedEpic.getSubTasksIds()) {
            subTaskArrayList.add(subTasks.get(subTaskId));
        }
        return subTaskArrayList;
    }

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
        ArrayList<Integer> subTasksIdsOfEpic = epic.getSubTasksIds();
        for (Integer subTaskId : subTasksIdsOfEpic) {
            if (!Status.DONE.equals(subTasks.get(subTaskId).getStatus())) {
                return false;
            }
        }
        return true;
    }

    private boolean isEpicAllNew(Epic epic) {
        ArrayList<Integer> subTasksIdsOfEpic = epic.getSubTasksIds();
        for (Integer subTaskId : subTasksIdsOfEpic) {
            if (!Status.NEW.equals(subTasks.get(subTaskId).getStatus())) {
                return false;
            }
        }
        return true;
    }
}
