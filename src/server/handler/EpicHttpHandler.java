package server.handler;

import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import service.TaskManager;
import service.exception.ManagerSaveException;
import service.exception.ValidationException;

import java.io.IOException;
import java.io.InputStreamReader;

public class EpicHttpHandler extends BaseHttpHandler {

    public EpicHttpHandler(TaskManager taskManager) {
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
                    sendText(exchange, gson.toJson(taskManager.getEpics()), 200);
                } else if (pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        Epic epic = taskManager.getEpic(id);
                        if (epic != null) {
                            sendText(exchange, gson.toJson(epic), 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                } else if (pathParts.length == 4) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        Epic epic = taskManager.getEpic(id);
                        if (epic != null) {
                            sendText(exchange, gson.toJson(epic.getSubTasksIds()), 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                }
            } else if ("POST".equalsIgnoreCase(method)) {
                try (InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), DEFAULT_CHARSET)) {
                    Epic epic = gson.fromJson(reader, Epic.class);
                    if (epic.getId() == null) {
                        taskManager.createEpic(epic);
                    } else {
                        taskManager.updateEpic(epic);
                    }
                    sendText(exchange, gson.toJson(epic), 201);
                } catch (ValidationException e) {
                    sendHasInteractions(exchange);
                }
            } else if ("DELETE".equalsIgnoreCase(method)) {
                if (pathParts.length == 3) {
                    try {
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteByIdEpic(id);
                        sendText(exchange, "{\"code\":200,\"message\":\"Epic deleted\"}", 200);
                    } catch (NumberFormatException e) {
                        sendNotFound(exchange);
                    }
                }
            } else {
                sendMethodNotAllowed(exchange);
            }
        } catch (ManagerSaveException e) {
            sendText(exchange, "{\"code\":500,\"message\":\"Error saving task\"}", 500);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }

    }
}
