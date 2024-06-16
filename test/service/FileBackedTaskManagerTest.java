package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private File file;

    @Override
    FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("tasks", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileBackedTaskManager(Managers.getDefaultHistory(), file);
    }

    @Test
    @DisplayName("Loading from saved version of TaskManager")
    public void shouldLoadFromSavedVersion() throws IOException {
        TaskManager restoredManager = FileBackedTaskManager.loadFromFileStatic(file);
        assertNotNull(restoredManager);
    }

    @Test
    @DisplayName("Tasks from loaded TaskManager are the same as TaskManager in memory")
    public void shouldLoadedBeTheSameAsTaskManagerInMemory() throws IOException {
        TaskManager restoredManager = FileBackedTaskManager.loadFromFileStatic(file);
        assertEquals(taskManager.getTasks(), restoredManager.getTasks());
        assertEquals(taskManager.getEpics(), restoredManager.getEpics());
        assertEquals(taskManager.getSubTasks(), restoredManager.getSubTasks());
    }

    @Test
    @DisplayName("loading from an empty save file")
    public void shouldLoadFromEmptyFile() throws IOException {
        File fileTemp = File.createTempFile("tasks", ".csv");
        TaskManager restoredManager = FileBackedTaskManager.loadFromFileStatic(fileTemp);
        restoredManager.deleteAllEpics();
        TaskManager restoredManager2 = FileBackedTaskManager.loadFromFileStatic(fileTemp);
        assertEquals(restoredManager.getTasks(), restoredManager2.getTasks());
        assertEquals(restoredManager.getEpics(), restoredManager2.getEpics());
        assertEquals(restoredManager.getSubTasks(), restoredManager2.getSubTasks());
    }
}