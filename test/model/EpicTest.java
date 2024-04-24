package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    @DisplayName("Should equals to copy")
    public void ShouldEqualsToCopy() {
        Epic epic = new Epic("name", "desc");
        Epic epicCopy = new Epic("name", "desc");
        assertEquals(epic, epicCopy, "Epic should be the same");
    }

    private static void assertEqualsEpics(Epic expected, Epic actual, String message) {
        assertEquals(expected.getName(), actual.getName(), message + " id");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " description");
        assertArrayEquals(expected.getSubTasksIds().toArray(),
                actual.getSubTasksIds().toArray(), message + " subtasks ids");
    }

}