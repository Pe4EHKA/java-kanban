package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.exception.ValidationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
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
        Epic epic1 = new Epic(epic.getName(), epic.getDescription(), LocalDateTime.now().plusMinutes(50),
                Duration.ofMinutes(15));
        epic1.setId(epic.getId());
        epic1.addTask(subTask.getId());
        assertEquals(epic1, epic, "Epic should equal to his copy");
        taskManager.createTask(epic1);
        assertNotNull(taskManager.getTask(epic1.getId()));
    }

    @Test
    @DisplayName("All Subtasks Status NEW")
    public void shouldAllSubtasksStatusNew() {
        assertTrue(taskManager.getSubTasks().stream()
                .allMatch(subTask -> Status.NEW.equals(subTask.getStatus())));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("All Subtasks Status DONE")
    public void shouldAllSubtasksStatusDONE() {
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        assertTrue(taskManager.getSubTasks().stream()
                .allMatch(subTask -> Status.DONE.equals(subTask.getStatus())));
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    @DisplayName("Subtasks Status NEW and DONE")
    public void shouldSubtasksStatusNEWandDONE() {
        SubTask subTask2 = taskManager.createSubTask(new SubTask("name4", "desc4",
                epic.getId(), LocalDateTime.now().plusMinutes(50), Duration.ofMinutes(15)));
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        assertFalse(taskManager.getSubTasks().stream()
                .noneMatch(subTask -> Status.DONE.equals(subTask.getStatus())));
        assertFalse(taskManager.getSubTasks().stream()
                .noneMatch(subTask -> Status.NEW.equals(subTask.getStatus())));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Subtasks Status IN_PROGRESS")
    public void shouldSubtasksStatusIN_PROGRESS() {
        subTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask);
        assertTrue(taskManager.getSubTasks().stream()
                .allMatch(subTask -> Status.IN_PROGRESS.equals(subTask.getStatus())));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
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
    @DisplayName("Delete tasks one by one")
    public void shouldDeleteOneByOne() {
        taskManager.deleteByIdTask(task.getId());
        assertNull(taskManager.getTask(task.getId()));
        assertEquals(taskManager.getTasks().size(), 0);

        taskManager.deleteByIdSubTask(subTask.getId());
        assertNull(taskManager.getTask(subTask.getId()));
        assertEquals(taskManager.getSubTasks().size(), 0);

        taskManager.deleteByIdEpic(epic.getId());
        assertNull(taskManager.getTask(epic.getId()));
        assertEquals(taskManager.getEpics().size(), 0);
    }

    @Test
    @DisplayName("Delete all tasks")
    public void shouldDeleteAll() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubTasks();
        assertEquals(taskManager.getTasks().size(), 0);
        assertEquals(taskManager.getEpics().size(), 0);
        assertEquals(taskManager.getSubTasks().size(), 0);
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
    @DisplayName("History saving all data")
    public void shouldHistorySave() {
        assertNotNull(taskManager.getTask(task.getId()));
        assertNotNull(taskManager.getEpic(epic.getId()));
        assertNotNull(taskManager.getSubTask(subTask.getId()));
        assertEquals(taskManager.getHistory().size(), 3);
        List<Task> history = taskManager.getHistory();
        System.out.println(history);
        assertEquals(history.getFirst(), task, "Task should equal to his new copy");
        assertEquals(history.get(1), epic, "Epic should equal to his new copy");
        assertEquals(history.getLast(), subTask, "SubTask should equal to his new copy");
    }

    @Test
    @DisplayName("Epic is not able to be added to another Epic")
    public void shouldNotBeAddedEpicToActualEpic() {
        SubTask subTask1 = new SubTask("name1", "desc1", epic.getId(), LocalDateTime.now(),
                Duration.ofMinutes(15));
        subTask1.setId(epic.getId());
        subTask1.setName(epic.getName());
        subTask1.setDescription(epic.getDescription());
        assertNull(epic.addTask(subTask1.getId()));
    }

    @Test
    @DisplayName("SubTask is not able to be Epic it self")
    public void shouldNotBeAddedSubTaskToEpicItSelfStatement() {
        subTask.setId(epic.getId());
        subTask.setName(epic.getName());
        subTask.setDescription(epic.getDescription());
        assertNull(subTask.setEpicId(epic.getId()));
    }

    @Test
    @DisplayName("TaskManagerShouldFindAllAddedTasks")
    public void shouldFindAllAddedTasks() {
        Task task1 = taskManager.getTask(task.getId());
        assertEquals(task1, task, "Task should equal to itself");
        Epic epic1 = taskManager.getEpic(epic.getId());
        assertEquals(epic1, epic, "Epic should equal to itself");
        SubTask subTask1 = taskManager.getSubTask(subTask.getId());
        assertEquals(subTask1, subTask, "SubTask should equal to itself");
    }

    @Test
    @DisplayName("Test Validation of Tasks throw exceptions")
    public void shouldValidateTasks() {
        assertThrows(ValidationException.class, () -> {
            Task task1 = taskManager.createTask(new Task("name1", "desc1", LocalDateTime.now(),
                    Duration.ofMinutes(15)));
        }, "Task should throw exceptions due to validation");
    }

    @Test
    @DisplayName("Test Validation without throwing exception")
    public void shouldValidateTaskWithoutThrowingException() {
        assertDoesNotThrow(() -> {
            Task task1 = taskManager.createTask(new Task("name1", "desc1", LocalDateTime.now()
                    .plusMinutes(50),
                    Duration.ofMinutes(15)));
        });
    }
}