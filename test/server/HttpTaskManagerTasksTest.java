package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerTasksTest {

    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
        server.startServer();
    }

    @AfterEach
    public void shutDown() {
        server.stopServer();
    }

    @Test
    @DisplayName("Delete task by id")
    public void shouldDeleteTaskByIdOnServer() throws IOException, InterruptedException {
        Task task = taskManager.createTask(new Task("name1", "description1"
                , LocalDateTime.now(), Duration.ofMinutes(1)));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        JsonElement json = gson.fromJson(response.body(), JsonElement.class);
        JsonObject jsonObject = json.getAsJsonObject();
        String message = jsonObject.get("message").getAsString();

        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
        assertEquals("Task deleted", message);
    }

    @Test
    @DisplayName("Get task by id")
    public void shouldGetTaskByIdFromServer() throws IOException, InterruptedException {
        Task task = taskManager.createTask(new Task("name1", "description1"
                , LocalDateTime.now(), Duration.ofMinutes(1)));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        Task taskFromJson = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(task, taskFromJson);
    }

    @Test
    @DisplayName("Get all tasks")
    public void shouldGetAllTasksFromServer() throws IOException, InterruptedException {
        Task task1 = taskManager.createTask(new Task("name1", "description1"
                , LocalDateTime.now(), Duration.ofMinutes(1)));
        Task task2 = taskManager.createTask(new Task("name2", "description2"
                , LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(1)));
        Task task3 = taskManager.createTask(new Task("name3", "description3"
                , LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(1)));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getTasks().size(), tasks.size());
        assertEquals(taskManager.getTasks(), tasks);
    }

    @Test
    @DisplayName("Update task on server")
    public void shouldUpdateTaskOnServer() throws IOException, InterruptedException {
        Task task1 = taskManager.createTask(new Task("name1", "description1"
                , LocalDateTime.now(), Duration.ofMinutes(1)));
        Task taskUpdated = new Task("nameUpdated", "descriptionUpdated",
                task1.getStartTime().plusMinutes(15), Duration.ofMinutes(15));
        taskUpdated.setId(task1.getId());
        String taskJson = gson.toJson(taskUpdated);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        Task taskFromJson = gson.fromJson(response.body(), Task.class);

        assertEquals(201, response.statusCode());
        assertEquals(taskUpdated, taskFromJson);
        assertEquals(taskUpdated, taskManager.getTask(taskUpdated.getId()));
    }

    @Test
    @DisplayName("Add task to server")
    public void shouldAddTaskOnServer() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }
}