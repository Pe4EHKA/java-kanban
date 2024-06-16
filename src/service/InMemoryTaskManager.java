package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.exception.ValidationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int seq = 0;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager;
    protected TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    private int generateId() {
        return ++seq;
    }  // Генерация идентификатора.

    protected List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private void addPrioritized(Task task) {
        boolean isOverlapping = getPrioritizedTasks().stream()
                .anyMatch(prioritizedTask -> tasksOverlap(prioritizedTask, task));
        if (isOverlapping) {
            throw new ValidationException("Tasks overlapping!");
        }
        if (task.getStartTime() == null) {
            throw new ValidationException("Task start time is null!");
        }
        prioritizedTasks.add(task);
    }

    private boolean tasksOverlap(Task task1, Task task2) {
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();
        return !(end1.isBefore(start2) || end1.isEqual(start2) || start1.isAfter(end2) || start1.isEqual(end2));
    }

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
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subTasks.keySet().forEach(historyManager::remove);
        subTasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.keySet().forEach(historyManager::remove);
        subTasks.clear();
        epics.values().forEach(epic -> {
            epic.removeAllTasks();
            updateEpicStatus(epic);
            recalculationOfEpicTime(epic);
        });
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
        addPrioritized(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        recalculationOfEpicTime(epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        Epic savedEpic = epics.get(subTask.getEpicId());
        savedEpic.addTask(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(savedEpic);
        recalculationOfEpicTime(savedEpic);
        addPrioritized(subTask);
        return subTask;
    }

    @Override
    public void updateTask(Task task) {  // Обновление.
        // Новая версия объекта с верным идентификатором передаётся в виде параметра.
        Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            throw new IllegalStateException("Task with id " + task.getId() + " does not exist");
        }
        prioritizedTasks.remove(savedTask);
        addPrioritized(savedTask);
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
        prioritizedTasks.remove(subTasks.get(subTask.getId()));
        addPrioritized(subTask);
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(savedEpic);
        recalculationOfEpicTime(savedEpic);
    }

    @Override
    public void deleteByIdTask(int id) {  // Удаление по идентификатору.
        Task taskToDelete = tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(taskToDelete);
    }

    @Override
    public void deleteByIdEpic(int id) {
        Epic savedEpic = epics.remove(id);
        if (savedEpic == null) {
            throw new IllegalStateException("Epic with id " + id + " does not exist");
        }
        historyManager.remove(savedEpic.getId());
        for (Integer subTasksIds : savedEpic.getSubTasksIds()) {
            SubTask subTaskToDelete = subTasks.remove(subTasksIds);
            prioritizedTasks.remove(subTaskToDelete);
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
        recalculationOfEpicTime(savedEpic);
        prioritizedTasks.remove(subTask);
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

    private void updateEpicStatus(Epic epic) {  // Расчитывание статуса Эпика
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
        return subTasksIdsOfEpic.stream().allMatch(subTaskId -> Status.DONE.equals(subTasks.get(subTaskId).getStatus()));
    }

    private boolean isEpicAllNew(Epic epic) {
        List<Integer> subTasksIdsOfEpic = epic.getSubTasksIds();
        return subTasksIdsOfEpic.stream().allMatch(subTaskId -> Status.NEW.equals(subTasks.get(subTaskId).getStatus()));
    }

    protected void recalculationOfEpicTime(Epic epic) {
        List<Integer> SubTasksIds = epic.getSubTasksIds();
        if (SubTasksIds.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.ofMinutes(15);
            epic.setStartTime(now);
            epic.setDuration(duration);
            epic.setEndTime(now.plus(duration));
        } else {
            LocalDateTime maxTime = LocalDateTime.MAX;
            LocalDateTime minTime = LocalDateTime.MIN;
            for (Integer subTasksId : SubTasksIds) {
                SubTask subTask = subTasks.get(subTasksId);
                LocalDateTime SubTaskStartTime = subTask.getStartTime();
                LocalDateTime SubTaskEndTime = subTask.getEndTime();

                if (SubTaskStartTime.isBefore(maxTime)) {
                    epic.setStartTime(SubTaskStartTime);
                    maxTime = SubTaskStartTime;
                }
                if (SubTaskEndTime.isAfter(minTime)) {
                    epic.setEndTime(SubTaskEndTime);
                    minTime = SubTaskEndTime;
                }
            }
        }
        Duration duration = Duration.between(epic.getStartTime(), epic.getEndTime());
        epic.setDuration(duration);
    }
}
