package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubTaskTest {

    @Test
    @DisplayName("Should equals to copy")
    public void shouldEqualsToCopy() {
        Epic epic = new Epic("name", "desc", LocalDateTime.now(), Duration.ofMinutes(15));
        epic.setId(1);
        SubTask subTask1 = new SubTask("name", "desc", epic.getId(), LocalDateTime.now(), Duration.ofMinutes(15));
        subTask1.setId(2);
        SubTask subTask2 = new SubTask("name", "desc", epic.getId(), LocalDateTime.now(), Duration.ofMinutes(15));
        subTask2.setId(2);
        assertEqualsEpics(subTask1, subTask2, "SubTasks should be the same");
    }

    private static void assertEqualsEpics(SubTask expected, SubTask actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + " id");
        assertEquals(expected.getName(), actual.getName(), message + " name");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " status");
        assertEquals(expected.getEpicId(), actual.getEpicId(), message + " epic id");
    }

}