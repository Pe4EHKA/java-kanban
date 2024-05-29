package service;

import converter.TaskConverter;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final TaskConverter taskConverter;
    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.taskConverter = new TaskConverter();
        this.file = file;
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
        this.taskConverter = new TaskConverter();
        this.file = new File("tasks.csv");
    }

    public static FileBackedTaskManager loadFromFileStatic(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);
        fileBackedTaskManager.loadFromFile();
        return fileBackedTaskManager;
    }

    private void loadFromFile() {
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                final Task task = taskConverter.fromString(line);
                final int id = task.getId();
                if (TaskType.TASK.equals(task.getType())) {
                    tasks.put(id, task);
                } else if (TaskType.EPIC.equals(task.getType())) {
                    epics.put(id, (Epic) task);
                } else if (TaskType.SUBTASK.equals(task.getType())) {
                    subTasks.put(id, (SubTask) task);
                }
                maxId = Math.max(maxId, id);
            }
            for (SubTask subTask : subTasks.values()) {
                Epic savedEpic = epics.get(subTask.getEpicId());
                if (savedEpic == null) {
                    throw new IllegalStateException("Epic of subTask with id " + subTask.getId() + " does not exist");
                }
                savedEpic.addTask(subTask.getId());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred during loading from file, path: " + file.getAbsolutePath(), e);
        }
        seq = maxId;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            String data = "id,type,name,status,description,epic";
            writer.write(data);
            writer.newLine();
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                data = taskConverter.toString(entry.getValue());
                writer.append(data);
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                data = taskConverter.toString(entry.getValue());
                writer.append(data);
                writer.newLine();
            }
            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                data = taskConverter.toString(entry.getValue());
                writer.append(data);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Error occurred during saving to file, path: " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public Task createTask(Task task) {
        Task taskCreated = super.createTask(task);
        save();
        return taskCreated;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic epicCreated = super.createEpic(epic);
        save();
        return epicCreated;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask subTaskCreated = super.createSubTask(subTask);
        save();
        return subTaskCreated;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteByIdTask(int id) {
        super.deleteByIdTask(id);
        save();
    }

    @Override
    public void deleteByIdEpic(int id) {
        super.deleteByIdEpic(id);
        save();
    }

    @Override
    public void deleteByIdSubTask(int id) {
        super.deleteByIdSubTask(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }
}
