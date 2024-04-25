package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    @DisplayName("Should be created initialized TaskManager class")
    public void shouldBeCreatedNormalTaskManager() {
        assertNotNull(Managers.getDefaults());
    }

    @Test
    @DisplayName("Should be created initialized History class")
    public void shouldBeCreatedNormalHistoryManager() {
        assertNotNull(Managers.getDefaultHistory());
    }
}