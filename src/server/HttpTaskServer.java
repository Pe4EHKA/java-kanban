package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import server.adapter.DurationTypeAdapter;
import server.adapter.LocalDateTimeTypeAdapter;
import server.handler.*;
import service.FileBackedTaskManager;
import service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/tasks", new TaskHttpHandler(taskManager));
        server.createContext("/subtasks", new SubTaskHttpHandler(taskManager));
        server.createContext("/epics", new EpicHttpHandler(taskManager));
        server.createContext("/history", new HistoryHttpHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHttpHandler(taskManager));
    }

    public void startServer() {
        server.start();
        System.out.println("Server started at port " + PORT);
    }

    public void stopServer() {
        server.stop(0);
        System.out.println("Server stopped at port: " + PORT);
    }


    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer(FileBackedTaskManager
                .loadFromFileStatic(new File("resources", "tasks.csv")));
        httpTaskServer.startServer();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .serializeNulls();
        return gsonBuilder.create();
    }
}


