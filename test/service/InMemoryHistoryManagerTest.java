package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {

    @Test
    @DisplayName("Should save old copy of task")
    public void shouldSaveOldCopyOfTask() {
        TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        Task task = taskManager.createTask(new Task("name", "desc"));
        taskManager.getTask(task.getId());
        task.setDescription("desc123");
        task.setName("name123");
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        taskManager.getTask(task.getId());
        List<Task> historyTasks = taskManager.getHistory();
        assertEquals(historyTasks.size(), 2);  // Просмотрено задач 2 раза
        // Проверка на то, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        assertEquals(historyTasks.get(0).getId(), task.getId());
        assertEquals(historyTasks.get(1).getId(), task.getId());
    }

    @Test
    @DisplayName("History contains only 10 tasks")
    public void shouldContainOnly10Tasks() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = new InMemoryTaskManager(historyManager);
        Task task = taskManager.createTask(new Task("name", "desc"));
        for (int i = 0; i < 12; i++) {
            taskManager.getTask(task.getId());
        }
        List<Task> historyTasks = historyManager.getHistory();
        assertEquals(historyTasks.size(), 10);
    }
}