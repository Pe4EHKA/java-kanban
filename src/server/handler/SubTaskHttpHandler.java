package server.handler;

import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import service.TaskManager;
import service.exception.ManagerSaveException;
import service.exception.ValidationException;

import java.io.IOException;
import java.io.InputStreamReader;

public class SubTaskHttpHandler extends BaseHttpHandler {

    public SubTaskHttpHandler(TaskManager taskManager) {
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
                    sendText(exchange, gson.toJson(taskManager.getSubTasks()), 200);
                } else if (pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        SubTask subTask = taskManager.getSubTask(id);
                        if (subTask != null) {
                            sendText(exchange, gson.toJson(subTask), 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), DEFAULT_CHARSET)) {
                    SubTask subTask = gson.fromJson(reader, SubTask.class);
                    if (subTask.getId() == null) {
                        taskManager.createSubTask(subTask);
                    } else {
                        taskManager.updateSubTask(subTask);
                    }
                    sendText(exchange, gson.toJson(subTask), 201);
                } catch (ValidationException e) {
                    sendHasInteractions(exchange);
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                if (pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteByIdSubTask(id);
                        sendText(exchange, "{\"code\":200,\"message\":\"SubTask deleted\"}", 200);
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
