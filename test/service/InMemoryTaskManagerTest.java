package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private final EmptyHistoryManager emptyHistoryManager = new EmptyHistoryManager();
    private InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager(emptyHistoryManager);
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    @DisplayName("Initialize of start point")
    public void init() {
        inMemoryTaskManager = new InMemoryTaskManager(emptyHistoryManager);

        task = inMemoryTaskManager.createTask(new Task("name1", "desc1"));
        epic = inMemoryTaskManager.createEpic(new Epic("name2", "desc2"));
        subTask = inMemoryTaskManager.createSubTask(new SubTask("name3", "desc3", epic.getId()));
    }

    @Test
    @DisplayName("Create Task")
    public void shouldCreateTaskAndEqualsToHimself() {
        Task task1 = new Task(task.getName(), task.getDescription());
        task1.setId(task.getId());
        assertEqualsTasks(task1, task, "Task should equal to his copy");
    }

    @Test
    @DisplayName("Create Epic")
    public void shouldCreateEpicAndEqualsToHimself() {
        Epic epic1 = new Epic(epic.getName(), epic.getDescription());
        epic1.setId(epic.getId());
        epic1.addTask(subTask.getId());
        assertEqualsTasks(epic1, epic, "Epic should equal to his copy");
    }

    @Test
    @DisplayName("Create SubTask")
    public void shouldCreateSubTaskAndEqualsToHimself() {
        SubTask subTask1 = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getEpicId());
        subTask1.setId(subTask.getId());
        assertEqualsTasks(subTask1, subTask, "SubTask should equal to his copy");
    }

    @Test
    @DisplayName("Epic is not able to be added to another Epic")
    public void shouldNotBeAddedEpicToActualEpic() {
        SubTask subTask1 = new SubTask("name1", "desc1", epic.getId());
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
        Task task1 = inMemoryTaskManager.getTask(task.getId());
        assertEqualsTasks(task1, task, "Task should equal to itself");
        Epic epic1 = inMemoryTaskManager.getEpic(epic.getId());
        assertEqualsTasks(epic1, epic, "Epic should equal to itself");
        SubTask subTask1 = inMemoryTaskManager.getSubTask(subTask.getId());
        assertEqualsTasks(subTask1, subTask, "SubTask should equal to itself");
    }

    private static void assertEqualsTasks(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + " id");
        assertEquals(expected.getName(), actual.getName(), message + " name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " description");
        if (expected instanceof Epic epicExpected && actual instanceof Epic epicActual) {
            assertArrayEquals(epicExpected.getSubTasksIds().toArray(), epicActual.getSubTasksIds().toArray(), message + " subTasks ids");
        } else if (expected instanceof SubTask subTaskExpected && actual instanceof SubTask subTaskActual) {
            assertEquals(subTaskExpected.getEpicId(), subTaskActual.getEpicId(), message + " epic id");
        }
    }

    private static class EmptyHistoryManager implements HistoryManager {

        @Override
        public void add(Task task) {

        }

        @Override
        public List<Task> getHistory() {
            return Collections.emptyList();
        }
    }
}