package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    @DisplayName("Should be created initialized class")
    public void ShouldBeCreatedNormalTaskManager() {
        TaskManager TaskManager = Managers.getDefaults();
        assertNotNull(TaskManager);
    }

}