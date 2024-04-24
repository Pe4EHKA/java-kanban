package service;

import model.Status;
import model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InMemoryHistoryManagerTest {

    @Test
    @DisplayName("Should save old copy of task")
    public void shouldSaveOldCopyOfTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = new InMemoryTaskManager(historyManager);
        Task task = taskManager.createTask(new Task("name", "desc"));
        Task taskSeen = taskManager.getTask(task.getId());
        task.setDescription("desc123");
        task.setName("name123");
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        Task taskSeen2 = taskManager.getTask(task.getId());
        List<Task> historyTasks = historyManager.getHistory();
        assertEquals(historyTasks.size(), 2);  // Просмотрено задач 2 раза
        // Проверка на то, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        assertNotEquals(historyTasks.get(0).getName(), historyTasks.get(1).getName());
        assertNotEquals(historyTasks.get(0).getDescription(), historyTasks.get(1).getDescription());
        assertNotEquals(historyTasks.get(0).getStatus(), historyTasks.get(1).getStatus());
    }
}