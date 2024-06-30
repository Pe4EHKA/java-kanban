package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.HttpTaskServer;
import service.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = HttpTaskServer.getGson();
    }

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        try (exchange) {
            byte[] bytesResponse = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(code, bytesResponse.length);
            exchange.getResponseBody().write(bytesResponse);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        try (exchange) {
            byte[] bytesResponse = "{\"code\":404,\"message\":\"Not Found\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(404, bytesResponse.length);
            exchange.getResponseBody().write(bytesResponse);
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        try (exchange) {
            byte[] bytesResponse = "{\"code\":406,\"message\":\"Tasks interacts with each-other\"}"
                    .getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(406, bytesResponse.length);
            exchange.getResponseBody().write(bytesResponse);
        }
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        try (exchange) {
            byte[] bytesResponse = "{\"code\":500,\"message\":\"Internal Error\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(500, bytesResponse.length);
            exchange.getResponseBody().write(bytesResponse);
        }
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        try (exchange) {
            byte[] bytesResponse = "{\"code\":405,\"message\":\"Method Not Allowed\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(405, bytesResponse.length);
            exchange.getResponseBody().write(bytesResponse);
        }
    }
}
