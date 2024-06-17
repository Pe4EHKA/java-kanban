package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

    abstract T createTaskManager();

    protected Task task;
    protected Epic epic;
    protected SubTask subTask;

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
        task = taskManager.createTask(new Task("name1", "desc1", LocalDateTime.now(),
                Duration.ofMinutes(15)));
        epic = taskManager.createEpic(new Epic("name2", "desc2"));
        subTask = taskManager.createSubTask(new SubTask("name3", "desc3", epic.getId(), LocalDateTime
                .now().plusMinutes(30), Duration.ofMinutes(15)));
    }

    @Test
    void getTasks() {
        assertEquals(List.of(task), taskManager.getTasks());
    }

    @Test
    void getEpics() {
        assertEquals(List.of(epic), taskManager.getEpics());
    }

    @Test
    void getSubTasks() {
        assertEquals(List.of(subTask), taskManager.getSubTasks());
    }

    @Test
    void getHistory() {
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubTask(3);
        assertEquals(List.of(task, epic, subTask), taskManager.getHistory());
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        assertEquals(List.of(), taskManager.getTasks());
    }

    @Test
    void deleteAllEpics() {
        taskManager.deleteAllEpics();
        assertEquals(List.of(), taskManager.getEpics());
    }

    @Test
    void deleteAllSubTasks() {
        taskManager.deleteAllSubTasks();
        assertTrue(taskManager.getSubTasks().isEmpty());
    }

    @Test
    void getTask() {
        assertEquals(task, taskManager.getTask(1));
    }

    @Test
    void getEpic() {
        assertEquals(epic, taskManager.getEpic(2));
    }

    @Test
    void getSubTask() {
        assertEquals(subTask, taskManager.getSubTask(3));
    }

    @Test
    void createTask() {
        Task task1 = taskManager.createTask(new Task("name4", "desc4", LocalDateTime.now()
                .plusMinutes(50), Duration.ofMinutes(15)));
        assertEquals(task1, taskManager.getTask(4));
    }

    @Test
    void createEpic() {
        Epic epic1 = taskManager.createEpic(new Epic("name4", "desc4"));
        assertEquals(epic1, taskManager.getEpic(4));
    }

    @Test
    void createSubTask() {
        SubTask subTask1 = taskManager.createSubTask(new SubTask("name4", "desc4", epic.getId(),
                LocalDateTime.now().plusMinutes(50), Duration.ofMinutes(15)));
        assertEquals(subTask1, taskManager.getSubTask(4));
    }

    @Test
    void updateTask() {
        task.setName("newName");
        task.setDescription("newDescription");
        taskManager.updateTask(task);
        assertEquals(task, taskManager.getTask(1));
    }

    @Test
    void updateEpic() {
        epic.setName("newEpic");
        epic.setDescription("newDescription");
        taskManager.updateEpic(epic);
        assertEquals(epic, taskManager.getEpic(2));
    }

    @Test
    void updateSubTask() {
        subTask.setName("newSubTask");
        subTask.setDescription("newDescription");
        taskManager.updateSubTask(subTask);
        assertEquals(subTask, taskManager.getSubTask(3));
    }

    @Test
    void deleteByIdTask() {
        taskManager.deleteByIdTask(1);
        assertEquals(List.of(), taskManager.getTasks());
    }

    @Test
    void deleteByIdEpic() {
        taskManager.deleteByIdEpic(2);
        assertEquals(List.of(), taskManager.getEpics());
    }

    @Test
    void deleteByIdSubTask() {
        taskManager.deleteByIdSubTask(3);
        assertEquals(List.of(), taskManager.getSubTasks());
    }

    @Test
    void getAllSubStacksByEpicId() {
        assertEquals(List.of(subTask), taskManager.getAllSubStacksByEpicId(2));
    }
}