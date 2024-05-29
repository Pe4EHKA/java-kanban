package converter;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class TaskConverter {

    public String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() +
                "," + task.getDescription() + "," + task.getEpicId();
    }

    public Task fromString(String string) {
        List<String> data = new ArrayList<>(List.of(string.split(",")));
        int id = Integer.parseInt(data.get(0));
        TaskType type = TaskType.valueOf(data.get(1));
        String name = data.get(2);
        Status status = Status.valueOf(data.get(3));
        String description = data.get(4);

        Task task = switch (type) {
            case TASK -> new Task(name, description);
            case EPIC -> new Epic(name, description);
            case SUBTASK -> {
                int epicId = Integer.parseInt(data.get(5));
                yield new SubTask(name, description, epicId);
            }
        };
        task.setId(id);
        task.setStatus(status);
        return task;
    }
}
