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