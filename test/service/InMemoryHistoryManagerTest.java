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

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    @DisplayName("Initialize of start point")
    public void init() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("name1", "desc1", LocalDateTime.now(), Duration.ofMinutes(15));
        task.setId(1);
        epic = new Epic("name2", "desc2");
        epic.setId(2);
        subTask = new SubTask("name3", "desc3", epic.getId(), LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(15));
        subTask.setId(3);
    }

    @Test
    @DisplayName("Should save old copy of task")
    public void shouldSaveOldCopyOfTask() {
        historyManager.add(task);
        task.setDescription("desc123");
        task.setName("name123");
        task.setStatus(Status.DONE);
        historyManager.add(task);
        List<Task> historyTasks = historyManager.getHistory();
        assertEquals(1, historyTasks.size());  // Просмотрена задача 2 раза, но в истории только одна запись.
        // Проверка на то, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        assertEquals(task.getId(), historyTasks.get(0).getId());
    }

    @Test
    @DisplayName("History contains only 1 task")
    public void shouldContainOnly10Tasks() {
        for (int i = 0; i < 12; i++) {
            historyManager.add(task);
        }
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("History contains tasks in specific order")
    public void shouldContainTasksInOrder() {
        historyManager.add(subTask);
        historyManager.add(epic);
        historyManager.add(task);
        assertEquals(List.of(subTask, epic, task), historyManager.getHistory());
    }

    @Test
    @DisplayName("History delete subtasks of epic")
    public void shouldDeleteSubtasksOfEpic() {
        TaskManager taskManager = new InMemoryTaskManager(historyManager);
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getTask(task.getId());
        taskManager.deleteAllEpics();
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().getFirst());
        assertEquals(task, historyManager.getHistory().getLast());
    }

    @Test
    @DisplayName("History remove nodes from linked list")
    public void shouldRemoveNodesFromLinkedList() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);
        assertEquals(3, historyManager.getHistory().size());
        historyManager.remove(task.getId());
        assertEquals(2, historyManager.getHistory().size());
        historyManager.remove(subTask.getId());
        assertEquals(1, historyManager.getHistory().size());
        historyManager.remove(epic.getId());
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    @DisplayName("History after deleting in the beginning should have right order of elements")
    public void shouldOrderAfterDeletingInTheBeginning() {
        historyManager.add(task);
        historyManager.add(subTask);
        historyManager.add(epic);
        historyManager.remove(task.getId());
        assertEquals(List.of(subTask, epic), historyManager.getHistory());
    }

    @Test
    @DisplayName("History after deleting in the middle should have right order of elements")
    public void shouldOrderAfterDeletingInTheMiddle() {
        historyManager.add(task);
        historyManager.add(subTask);
        Task task4 = new Task("name4", "desc4", LocalDateTime.now(), Duration.ofMinutes(15));
        task4.setId(4);
        historyManager.add(task4);
        historyManager.add(epic);
        historyManager.remove(task4.getId());
        assertEquals(List.of(task, subTask, epic), historyManager.getHistory());
    }

    @Test
    @DisplayName("History after deleting in the ending should have right order of elements")
    public void shouldOrderAfterDeletingInTheEnd() {
        historyManager.add(epic);
        historyManager.add(subTask);
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertEquals(List.of(epic, subTask), historyManager.getHistory());
    }
}