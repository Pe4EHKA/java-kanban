package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    @DisplayName("Get all Tasks")
    void shouldGetTasks() {
        assertEquals(List.of(task), taskManager.getTasks());
    }

    @Test
    @DisplayName("Get all Epics")
    void shouldGetEpics() {
        assertEquals(List.of(epic), taskManager.getEpics());
    }

    @Test
    @DisplayName("Get all SubTasks")
    void shouldGetSubTasks() {
        assertEquals(List.of(subTask), taskManager.getSubTasks());
    }

    @Test
    @DisplayName("Get History")
    void shouldGetHistory() {
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubTask(3);
        assertEquals(List.of(task, epic, subTask), taskManager.getHistory());
    }

    @Test
    @DisplayName("Delete all Tasks")
    void shouldDeleteAllTasks() {
        taskManager.deleteAllTasks();
        assertEquals(taskManager.getTasks().size(), 0);
    }

    @Test
    @DisplayName("Delete all Epics")
    void shouldDeleteAllEpics() {
        taskManager.deleteAllEpics();
        assertEquals(taskManager.getEpics().size(), 0);
    }

    @Test
    @DisplayName("Delete all SubTasks")
    void shouldDeleteAllSubTasks() {
        taskManager.deleteAllSubTasks();
        assertEquals(taskManager.getSubTasks().size(), 0);
    }

    @Test
    @DisplayName("Get task")
    public void shouldGetTask() {
        assertNotNull(taskManager.getTask(task.getId()));
    }

    @Test
    @DisplayName("Get epic")
    public void shouldGetEpic() {
        assertNotNull(taskManager.getEpic(epic.getId()));
    }

    @Test
    @DisplayName("Get task")
    public void shouldGetSubTask() {
        assertNotNull(taskManager.getSubTask(subTask.getId()));
    }

    @Test
    @DisplayName("Create Task")
    public void shouldCreateTaskAndEqualsToHimself() {
        Task task1 = new Task(task.getName(), task.getDescription(), LocalDateTime.now().plusMinutes(55),
                Duration.ofMinutes(15));
        task1.setId(task.getId());
        assertEquals(task1, task, "Task should equal to his copy");
        taskManager.createTask(task1);
        assertNotNull(taskManager.getTask(task1.getId()));
    }

    @Test
    @DisplayName("Create Epic")
    public void shouldCreateEpicAndEqualsToHimself() {
        Epic epic1 = new Epic(epic.getName(), epic.getDescription());
        epic1.setId(epic.getId());
        epic1.addTask(subTask.getId());
        assertEquals(epic1, epic, "Epic should equal to his copy");
        taskManager.createEpic(epic1);
        assertNotNull(taskManager.getEpic(epic1.getId()));
    }

    @Test
    @DisplayName("Create SubTask")
    public void shouldCreateSubTaskAndEqualsToHimself() {
        SubTask subTask1 = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getEpicId(),
                LocalDateTime.now().plusMinutes(90), Duration.ofMinutes(15));
        subTask1.setId(subTask.getId());
        assertEquals(subTask1, subTask, "SubTask should equal to his copy");
        taskManager.createTask(subTask1);
        assertNotNull(taskManager.getTask(subTask1.getId()));
    }

    @Test
    @DisplayName("Update task")
    public void shouldUpdateTask() {
        assertEquals(task, taskManager.getTask(task.getId()), "Task should equal to his copy");
        task.setName("nameNew");
        task.setDescription("descriptionNew");
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        assertEquals(task, taskManager.getTask(task.getId()), "Task should equal to his new copy");
    }

    @Test
    @DisplayName("Update epic")
    public void shouldUpdateEpic() {
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Epic should equal to his copy");
        epic.setName("nameNew");
        epic.setDescription("descriptionNew");
        taskManager.updateEpic(epic);
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Epic should equal to his new copy");
    }

    @Test
    @DisplayName("Update subTask")
    public void shouldUpdateSubTask() {
        assertEquals(subTask, taskManager.getSubTask(subTask.getId()), "SubTask should equal to his copy");
        subTask.setName("nameNew");
        subTask.setDescription("descriptionNew");
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        assertEquals(subTask, taskManager.getSubTask(subTask.getId()), "SubTask should equal to his new copy");
    }

    @Test
    @DisplayName("Delete Task by id")
    void deleteByIdTask() {
        taskManager.deleteByIdTask(task.getId());
        assertNull(taskManager.getTask(task.getId()));
        assertEquals(taskManager.getTasks().size(), 0);
    }

    @Test
    @DisplayName("Delete Epic by id")
    void deleteByIdEpic() {
        taskManager.deleteByIdEpic(epic.getId());
        assertNull(taskManager.getTask(epic.getId()));
        assertEquals(taskManager.getEpics().size(), 0);
    }

    @Test
    @DisplayName("Delete SubTask by id")
    void deleteByIdSubTask() {
        taskManager.deleteByIdSubTask(subTask.getId());
        assertNull(taskManager.getTask(subTask.getId()));
        assertEquals(taskManager.getSubTasks().size(), 0);
    }

    @Test
    @DisplayName("Get All SubTasks by Epic id")
    void shouldGetAllSubStacksByEpicId() {
        assertEquals(List.of(subTask), taskManager.getAllSubStacksByEpicId(2));
    }

    @Test
    @DisplayName("Prioritized Tasks should be in order")
    public void shouldPrioritizeTasks() {
        Task taskNew = taskManager.createTask(new Task("name4", "description4", LocalDateTime.now()
                .minusMinutes(60),
                Duration.ofMinutes(15)));
        SubTask subTaskNew = taskManager.createSubTask(new SubTask("name5", "description5",
                epic.getId(), LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(10)));
        assertEquals(List.of(taskNew, task, subTaskNew, subTask), taskManager.getPrioritizedTasks());
    }
}