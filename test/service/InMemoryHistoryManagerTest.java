package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager inMemoryHistoryManager;
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    @DisplayName("Initialize of start point")
    public void init() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(inMemoryHistoryManager);

        task = taskManager.createTask(new Task("name1", "desc1"));
        epic = taskManager.createEpic(new Epic("name2", "desc2"));
        subTask = taskManager.createSubTask(new SubTask("name3", "desc3", epic.getId()));
    }

    @Test
    @DisplayName("Should save old copy of task")
    public void shouldSaveOldCopyOfTask() {
        taskManager.getTask(task.getId());
        task.setDescription("desc123");
        task.setName("name123");
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        taskManager.getTask(task.getId());
        List<Task> historyTasks = taskManager.getHistory();
        assertEquals(historyTasks.size(), 1);  // Просмотрена задача 2 раза, но в истории только одна запись.
        // Проверка на то, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        assertEquals(historyTasks.get(0).getId(), task.getId());
    }

    @Test
    @DisplayName("History contains only 1 task")
    public void shouldContainOnly10Tasks() {
        for (int i = 0; i < 12; i++) {
            taskManager.getTask(task.getId());
        }
        List<Task> historyTasks = inMemoryHistoryManager.getHistory();
        assertEquals(historyTasks.size(), 1);
    }

    @Test
    @DisplayName("History contains tasks in specific order")
    public void shouldContainTasksInOrder() {
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getTask(task.getId());
        ArrayList<Task> tasksInOrder = new ArrayList<>();
        tasksInOrder.add(subTask);
        tasksInOrder.add(epic);
        tasksInOrder.add(task);
        assertArrayEquals(inMemoryHistoryManager.getHistory().toArray(), tasksInOrder.toArray());
    }

    @Test
    @DisplayName("History delete subtasks of epic")
    public void shouldDeleteSubtasksOfEpic() {
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getTask(task.getId());
        taskManager.deleteAllEpics();
        ArrayList<Task> tasksInOrder = new ArrayList<>();
        tasksInOrder.add(task);
        assertEquals(inMemoryHistoryManager.getHistory().size(), tasksInOrder.size());
        assertEquals(inMemoryHistoryManager.getHistory().getFirst(), task);
        assertEquals(inMemoryHistoryManager.getHistory().getLast(), task);
    }

    @Test
    @DisplayName("History remove nodes from linked list")
    public void shouldRemoveNodesFromLinkedList() {
        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.add(subTask);
        assertEquals(inMemoryHistoryManager.getHistory().size(), 3);
        inMemoryHistoryManager.remove(task.getId());
        assertEquals(inMemoryHistoryManager.getHistory().size(), 2);
        inMemoryHistoryManager.remove(subTask.getId());
        assertEquals(inMemoryHistoryManager.getHistory().size(), 1);
        inMemoryHistoryManager.remove(epic.getId());
        assertEquals(inMemoryHistoryManager.getHistory().size(), 0);
    }
}