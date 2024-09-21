import com.google.gson.Gson;
import httpServer.HttpTaskServer;
import manager.ManagersUtils;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager taskManager = ManagersUtils.getDefault();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = taskServer.getGson();

    public HttpTaskServerTest() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI postUrl = URI.create("http://localhost:8080/tasks");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(postUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/1");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    public void testAllTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI postUrl = URI.create("http://localhost:8080/tasks");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(postUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, LocalDateTime.now().plusHours(2), Duration.ofMinutes(10));
        String taskJson1 = gson.toJson(task1);
        HttpRequest postRequest1 = HttpRequest.newBuilder()
                .uri(postUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();
        client.send(postRequest1, HttpResponse.BodyHandlers.ofString());
        URI getUrl = URI.create("http://localhost:8080/tasks/");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
        List<Task> tasksFromManager = taskManager.getTasks();
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testDelTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(10));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI postUrl = URI.create("http://localhost:8080/tasks");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(postUrl)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        // вызываем рест, отвечающий за создание задач
        client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/tasks/1");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .DELETE()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    public void testAddSubtaskAndEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Subtask subtask = new Subtask("Test", "Test",
                TaskStatus.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getEpics();
        List<Subtask> SubtasksFromManager = taskManager.getSubtasks();
        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertNotNull(SubtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
        assertEquals(1, SubtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Epic", tasksFromManager.getFirst().getName(), "Некорректное имя эпика");
        assertEquals("Test", SubtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubtaskAndEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("Test", "Test",
                TaskStatus.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/epics/1");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
        getUrl = URI.create("http://localhost:8080/subtasks/2");
        getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    public void testAllSubtaskAndEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("Test", "Test",
                TaskStatus.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/epics/");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
        getUrl = URI.create("http://localhost:8080/subtasks/");
        getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();
        getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    public void testDelSubtaskAndEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("Test", "Test",
                TaskStatus.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI getUrl = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .DELETE()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
        getUrl = URI.create("http://localhost:8080/epics/1");
        getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .DELETE()
                .build();
        getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    public void testGetSubtaskForEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("Test", "Test",
                TaskStatus.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        URI getUrl = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    public void testGetPrioritizedTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("Test", "Test",
                TaskStatus.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        URI getUrl = URI.create("http://localhost:8080/prioritized");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
    }

    @Test
    public void testGetHistoryTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "epic");
        String taskJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());

        Subtask subtask = new Subtask("Test", "Test",
                TaskStatus.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(10));
        taskJson = gson.toJson(subtask);
        url = URI.create("http://localhost:8080/subtasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        taskManager.getEpic(1);
        taskManager.getSubtask(2);
        URI getUrl = URI.create("http://localhost:8080/history");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUrl)
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, getResponse.statusCode());
    }
}

