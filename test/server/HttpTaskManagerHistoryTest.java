package server;

import com.google.gson.Gson;
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

class HttpTaskManagerHistoryTest {
    TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
    HttpTaskServer server = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() throws IOException {
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
    @DisplayName("Get History")
    public void shouldGetHistory() throws IOException, InterruptedException {
        Epic epic = taskManager.createEpic(new Epic("name", "description"));
        SubTask subTask1 = taskManager.createSubTask(new SubTask("name1", "description1",
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(1)));
        SubTask subTask2 = taskManager.createSubTask(new SubTask("name2", "description2",
                epic.getId(), LocalDateTime.now().plusMinutes(2), Duration.ofMinutes(1)));
        SubTask subTask3 = taskManager.createSubTask(new SubTask("name3", "description3",
                epic.getId(), LocalDateTime.now().plusMinutes(4), Duration.ofMinutes(1)));

        taskManager.getSubTask(subTask1.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.getSubTask(subTask3.getId());
        taskManager.getSubTask(subTask2.getId());

        HttpResponse<String> response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/history");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        List<SubTask> history = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getHistory(), history);
    }
}