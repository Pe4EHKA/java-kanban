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

class HttpTaskManagerEpicsTest {
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    @DisplayName("Delete Epic by id")
    public void shouldDeleteEpicByIdOnServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
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
        assertEquals(0, taskManager.getEpics().size());
        assertEquals("Epic deleted", message);
    }

    @Test
    @DisplayName("Get epic by id")
    public void shouldGetEpicByIdFromServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        Epic taskFromJson = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode());
        assertEquals(epic, taskFromJson);
    }

    @Test
    @DisplayName("Get all epics")
    public void shouldGetAllEpicsFromServer() throws IOException, InterruptedException {
        Epic epic1 = taskManager.createEpic(new Epic("name1", "description1"));
        Epic epic2 = taskManager.createEpic(new Epic("name2", "description2"));
        Epic epic3 = taskManager.createEpic(new Epic("name3", "description3"));


        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getEpics().size(), epics.size());
        assertEquals(taskManager.getEpics(), epics);
    }

    @Test
    @DisplayName("Get all subtasks from epic")
    public void shouldGetAllSubTasksFromEpicFromServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("name1", "description1",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(5)));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("name2", "description2",
                epic.getId(), LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5)));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("name3", "description3",
                epic.getId(), LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(5)));

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        List<Integer> subTasksIds = gson.fromJson(response.body(), new TypeToken<List<Integer>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getSubTasks().size(), subTasksIds.size());
        assertEquals(taskManager.getEpic(epic.getId()).getSubTasksIds(), subTasksIds);
    }

    @Test
    @DisplayName("Update epic on server")
    public void shouldUpdateEpicOnServer() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));
        Epic epicUpdated = taskManager.createEpic(new Epic("name2", "description2"));
        epicUpdated.setId(epic.getId());
        String subTaskJson = gson.toJson(epicUpdated);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        Epic epicFromJson = gson.fromJson(response.body(), Epic.class);

        assertEquals(201, response.statusCode());
        assertEquals(epicUpdated, epicFromJson);
        assertEquals(epicUpdated, taskManager.getEpic(epicUpdated.getId()));
    }

    @Test
    @DisplayName("Add Epic to server")
    public void shouldAddEpicOnServer() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2");
        SubTask subTask = new SubTask("Test 2", "Testing epic 2", 1,
                LocalDateTime.now(), Duration.ofMinutes(5));
        epic.setStartTime(subTask.getStartTime());
        epic.setDuration(subTask.getDuration());
        epic.setEndTime(subTask.getEndTime());
        String taskJson = gson.toJson(epic);

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/epics");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", epicsFromManager.get(0).getName(), "Некорректное имя задачи");
    }
}