package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
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

class HttpTaskManagerSubTasksTest {
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubTasksTest() throws IOException {
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
    @DisplayName("Delete subtask by id")
    public void shouldDeleteSubTaskByIdOnServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("name1", "description1",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(1)));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
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
        assertEquals(0, taskManager.getSubTasks().size());
        assertEquals("SubTask deleted", message);
    }

    @Test
    @DisplayName("Get subtask by id")
    public void shouldGetSubTaskByIdFromServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("name1", "description1",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(1)));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        SubTask taskFromJson = gson.fromJson(response.body(), SubTask.class);

        assertEquals(200, response.statusCode());
        assertEquals(subTask, taskFromJson);
    }

    @Test
    @DisplayName("Get all subtasks")
    public void shouldGetAllSubTasksFromServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("name1", "description1",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(1)));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("name2", "description2",
                epic.getId(), LocalDateTime.now().plusMinutes(2), Duration.ofMinutes(1)));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("name3", "description3",
                epic.getId(), LocalDateTime.now().plusMinutes(4), Duration.ofMinutes(1)));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        List<SubTask> subTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getSubTasks().size(), subTasks.size());
        assertEquals(taskManager.getSubTasks(), subTasks);
    }

    @Test
    @DisplayName("Update subtask on server")
    public void shouldUpdateSubTaskOnServerOnServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));
        SubTask subTask = taskManager.createSubTask(new SubTask("name1", "description1",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(1)));
        SubTask subTaskUpdated = new SubTask("nameUpdated", "descriptionUpdated", epic.getId(),
                subTask.getStartTime().plusMinutes(15), Duration.ofMinutes(15));
        subTaskUpdated.setId(subTask.getId());
        String subTaskJson = gson.toJson(subTaskUpdated);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        SubTask subTaskFromJson = gson.fromJson(response.body(), SubTask.class);

        assertEquals(201, response.statusCode());
        assertEquals(subTaskUpdated, subTaskFromJson);
        assertEquals(subTaskUpdated, taskManager.getSubTask(subTaskUpdated.getId()));
    }

    @Test
    @DisplayName("Add subtask to server")
    public void shouldAddSubTaskOnServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));
        SubTask subTask = new SubTask("Test 2", "Testing subtask 2", epic.getId(),
                LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(subTask);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = taskManager.getSubTasks();

        assertNotNull(subTasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subTasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }
}