package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileBackedTaskManagerTest {

    private TaskManager fileBackedTaskManager;
    private File file;
    private Task task;
    private Epic epic;
    private SubTask subTask;

    @BeforeEach
    @DisplayName("Initialize of start point")
    public void init() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(Managers.getDefaultHistory(), file);

        task = fileBackedTaskManager.createTask(new Task("name1", "desc1"));
        epic = fileBackedTaskManager.createEpic(new Epic("name2", "desc2"));
        subTask = fileBackedTaskManager.createSubTask(new SubTask("name3", "desc3", epic.getId()));
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
        assertEquals(fileBackedTaskManager.getTasks(), restoredManager.getTasks());
        assertEquals(fileBackedTaskManager.getEpics(), restoredManager.getEpics());
        assertEquals(fileBackedTaskManager.getSubTasks(), restoredManager.getSubTasks());
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