package service;

import model.Epic;
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
            epic.getSubTasks().clear();
            epic.updateStatus();
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
        epic.updateStatus();
        epics.put(epic.getId(), epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        Epic epic = subTask.getEpic();
        epic.addTask(subTask);
        epic.updateStatus();
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }

    public void updateTask(Task task) {  // Обновление.
        // Новая версия объекта с верным идентификатором передаётся в виде параметра.
        if (task == null) return;
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) return;
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        savedEpic.updateStatus();
        epics.put(savedEpic.getId(), savedEpic);
    }

    public void updateSubTask(SubTask subTask) {
        Epic epic = subTask.getEpic();
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) return;
        ArrayList<SubTask> subTasksOfEpic = savedEpic.getSubTasks();
        for (int i = 0; i < subTasksOfEpic.size(); i++) {
            if (subTasksOfEpic.get(i).equals(subTask)) {
                subTasksOfEpic.set(i, subTask);
                break;
            }
        }
        subTasks.put(subTask.getId(), subTask);
        savedEpic.updateStatus();
    }

    public void deleteByIdTask(int id) {  // Удаление по идентификатору.
        tasks.remove(id);
    }

    public void deleteByIdEpic(int id) {
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) return;
        for (SubTask subTask : savedEpic.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        savedEpic.getSubTasks().clear();
        epics.remove(id);
    }

    public void deleteByIdSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null) return;
        Epic savedEpic = subTask.getEpic();
        savedEpic.removeTask(subTask);
        savedEpic.updateStatus();
        subTasks.remove(id);
    }

    public ArrayList<SubTask> getAllSubStacksByEpicId(int id) {  // Получение списка всех подзадач определённого эпика.
        Epic savedEpic = epics.get(id);
        if (savedEpic == null) return null;
        return new ArrayList<>(savedEpic.getSubTasks());
    }


}
