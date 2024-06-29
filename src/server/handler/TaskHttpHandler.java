package server.handler;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.TaskManager;
import service.exception.ManagerSaveException;
import service.exception.ValidationException;

import java.io.IOException;
import java.io.InputStreamReader;

public class TaskHttpHandler extends BaseHttpHandler {

    public TaskHttpHandler(TaskManager taskManager) {
        super(taskManager);

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        try (exchange) {
            if ("GET".equalsIgnoreCase(method)) {
                if (pathParts.length == 2) {
                    sendText(exchange, gson.toJson(taskManager.getTasks()), 200);
                } else if (pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        Task task = taskManager.getTask(id);
                        if (task != null) {
                            sendText(exchange, gson.toJson(task), 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), DEFAULT_CHARSET)) {
                    Task task = gson.fromJson(reader, Task.class);
                    if (task.getId() == null) {
                        taskManager.createTask(task);
                    } else {
                        taskManager.updateTask(task);
                    }
                    sendText(exchange, gson.toJson(task), 201);
                } catch (ValidationException e) {
                    sendHasInteractions(exchange);
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                if (pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteByIdTask(id);
                        sendText(exchange, "{\"code\":200,\"message\":\"Task deleted\"}", 200);
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                }
            }
        } catch (ManagerSaveException e) {
            sendText(exchange, "{\"code\":500,\"message\":\"Error saving task\"}", 500);
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
