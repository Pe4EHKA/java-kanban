package converter;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskConverter {

    public static String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() +
                "," + task.getDescription() + "," + task.getEpicId() + "," + task.getDuration().toMinutes() + ","
                + task.getStartTime();
    }

    public static Task fromString(String string) {
        List<String> data = new ArrayList<>(List.of(string.split(",")));
        int id = Integer.parseInt(data.get(0));
        TaskType type = TaskType.valueOf(data.get(1));
        String name = data.get(2);
        Status status = Status.valueOf(data.get(3));
        String description = data.get(4);
        Duration duration = Duration.ofMinutes(Integer.parseInt(data.get(6)));
        LocalDateTime startTime = LocalDateTime.parse(data.get(7));

        Task task = switch (type) {
            case TASK -> new Task(name, description, startTime, duration);
            case EPIC -> new Epic(name, description, startTime, duration);
            case SUBTASK -> {
                int epicId = Integer.parseInt(data.get(5));
                yield new SubTask(name, description, epicId, startTime, duration);
            }
        };
        task.setId(id);
        task.setStatus(status);
        return task;
    }
}
