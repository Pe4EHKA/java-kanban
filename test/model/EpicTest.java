package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    @DisplayName("Should equals to copy")
    public void shouldEqualsToCopy() {
        Epic epic = new Epic("name", "desc");
        epic.setId(1);
        Epic epicCopy = new Epic("name", "desc");
        epicCopy.setId(1);
        assertEqualsEpics(epic, epicCopy, "Epic should be the same");
    }

    private static void assertEqualsEpics(Epic expected, Epic actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + " id");
        assertEquals(expected.getName(), actual.getName(), message + " name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " status");
        assertArrayEquals(expected.getSubTasksIds().toArray(),
                actual.getSubTasksIds().toArray(), message + " subtasks ids");
    }

}