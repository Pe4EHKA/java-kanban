package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    @Test
    @DisplayName("Should equals to copy")
    public void shouldEqualsToCopy() {
        Task task1 = new Task("name", "desc");
        task1.setId(1);
        Task task2 = new Task("name", "desc");
        task2.setId(1);
        assertEqualsTasks(task1, task2, "SubTasks should be the same");
    }

    private static void assertEqualsTasks(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + " id");
        assertEquals(expected.getName(), actual.getName(), message + " name");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " status");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " description");
    }

}