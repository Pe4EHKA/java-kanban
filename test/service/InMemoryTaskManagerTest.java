package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager inMemoryTaskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    @DisplayName("Initialize of start point")
    public void init() {
        inMemoryTaskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

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
        inMemoryTaskManager.createTask(task1);
        assertNotNull(inMemoryTaskManager.getTask(task1.getId()));
    }

    @Test
    @DisplayName("Create Epic")
    public void shouldCreateEpicAndEqualsToHimself() {
        Epic epic1 = new Epic(epic.getName(), epic.getDescription());
        epic1.setId(epic.getId());
        epic1.addTask(subTask.getId());
        assertEqualsTasks(epic1, epic, "Epic should equal to his copy");
        inMemoryTaskManager.createTask(epic1);
        assertNotNull(inMemoryTaskManager.getTask(epic1.getId()));
    }

    @Test
    @DisplayName("Create SubTask")
    public void shouldCreateSubTaskAndEqualsToHimself() {
        SubTask subTask1 = new SubTask(subTask.getName(), subTask.getDescription(), subTask.getEpicId());
        subTask1.setId(subTask.getId());
        assertEqualsTasks(subTask1, subTask, "SubTask should equal to his copy");
        inMemoryTaskManager.createTask(subTask1);
        assertNotNull(inMemoryTaskManager.getTask(subTask1.getId()));
    }

    @Test
    @DisplayName("Delete tasks one by one")
    public void shouldDeleteOneByOne() {
        inMemoryTaskManager.deleteByIdTask(task.getId());
        assertNull(inMemoryTaskManager.getTask(task.getId()));
        assertEquals(inMemoryTaskManager.getTasks().size(), 0);

        inMemoryTaskManager.deleteByIdSubTask(subTask.getId());
        assertNull(inMemoryTaskManager.getTask(subTask.getId()));
        assertEquals(inMemoryTaskManager.getSubTasks().size(), 0);

        inMemoryTaskManager.deleteByIdEpic(epic.getId());
        assertNull(inMemoryTaskManager.getTask(epic.getId()));
        assertEquals(inMemoryTaskManager.getEpics().size(), 0);
    }

    @Test
    @DisplayName("Delete all tasks")
    public void shouldDeleteAll() {
        inMemoryTaskManager.deleteAllTasks();
        inMemoryTaskManager.deleteAllEpics();
        inMemoryTaskManager.deleteAllSubTasks();
        assertEquals(inMemoryTaskManager.getTasks().size(), 0);
        assertEquals(inMemoryTaskManager.getEpics().size(), 0);
        assertEquals(inMemoryTaskManager.getSubTasks().size(), 0);
    }

    @Test
    @DisplayName("Update task")
    public void shouldUpdateTask() {
        assertEqualsTasks(task, inMemoryTaskManager.getTask(task.getId()), "Task should equal to his copy");
        task.setName("nameNew");
        task.setDescription("descriptionNew");
        task.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(task);
        assertEqualsTasks(task, inMemoryTaskManager.getTask(task.getId()), "Task should equal to his new copy");
    }

    @Test
    @DisplayName("Update epic")
    public void shouldUpdateEpic() {
        assertEqualsTasks(epic, inMemoryTaskManager.getEpic(epic.getId()), "Epic should equal to his copy");
        epic.setName("nameNew");
        epic.setDescription("descriptionNew");
        inMemoryTaskManager.updateEpic(epic);
        assertEqualsTasks(epic, inMemoryTaskManager.getEpic(epic.getId()), "Epic should equal to his new copy");
    }

    @Test
    @DisplayName("Update subTask")
    public void shouldUpdateSubTask() {
        assertEqualsTasks(subTask, inMemoryTaskManager.getSubTask(subTask.getId()), "SubTask should equal to his copy");
        subTask.setName("nameNew");
        subTask.setDescription("descriptionNew");
        subTask.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubTask(subTask);
        assertEqualsTasks(subTask, inMemoryTaskManager.getSubTask(subTask.getId()), "SubTask should equal to his new copy");
    }

    @Test
    @DisplayName("Get task")
    public void shouldGetTask() {
        assertNotNull(inMemoryTaskManager.getTask(task.getId()));
    }

    @Test
    @DisplayName("Get epic")
    public void shouldGetEpic() {
        assertNotNull(inMemoryTaskManager.getEpic(epic.getId()));
    }

    @Test
    @DisplayName("Get task")
    public void shouldGetSubTask() {
        assertNotNull(inMemoryTaskManager.getSubTask(subTask.getId()));
    }

    @Test
    @DisplayName("History saving all data")
    public void shouldHistorySave() {
        assertNotNull(inMemoryTaskManager.getTask(task.getId()));
        assertNotNull(inMemoryTaskManager.getEpic(epic.getId()));
        assertNotNull(inMemoryTaskManager.getSubTask(subTask.getId()));
        assertEquals(inMemoryTaskManager.getHistory().size(), 3);
        List<Task> history = inMemoryTaskManager.getHistory();
        System.out.println(history);
        assertEqualsTasks(history.getFirst(), task, "Task should equal to his new copy");
        assertEqualsTasks(history.get(1), epic, "Epic should equal to his new copy");
        assertEqualsTasks(history.getLast(), subTask, "SubTask should equal to his new copy");
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
}