package server.handler;

import com.sun.net.httpserver.HttpExchange;
import service.TaskManager;

import java.io.IOException;

public class HistoryHttpHandler extends BaseHttpHandler {

    public HistoryHttpHandler(TaskManager taskManager) {
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
                    sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
                }
            } else {
                sendMethodNotAllowed(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}
