package web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskServerTest {

    private final String kvServerUrl = "http://localhost:" + KVServer.PORT;
    private final String taskServerUrl = "http://localhost:" + HTTPTaskServer.PORT;

    private KVServer managerServer;
    private HTTPTaskServer taskServer;
    private HttpClient client;
    private int idCounter = 1;
    private Gson gson;

    @BeforeEach
    public void startServer() throws IOException, InterruptedException {
        managerServer = new KVServer();
        managerServer.start();
        taskServer = new HTTPTaskServer(kvServerUrl);
        taskServer.start();
        client = HttpClient.newHttpClient();
        idCounter = 1;
        gson = new Gson();
    }

    @AfterEach
    public void stopServer() {
        taskServer.stop();
        managerServer.stop();
    }

    @Test
    public void handleTasksListQueryTest() throws IOException, InterruptedException {
        Task task = addTask(LocalDateTime.now());
        URI uri = URI.create(taskServerUrl + "/tasks/task");
        HttpRequest request;
        HttpResponse<String> response;

        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(List.of(task), List.of(gson.fromJson(response.body(), Task[].class)));

        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void handleEpicsListQueryTest() throws IOException, InterruptedException {
        Epic epic = addEpic();
        URI uri = URI.create(taskServerUrl + "/tasks/epic");
        HttpRequest request;
        HttpResponse<String> response;

        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(List.of(epic), List.of(gson.fromJson(response.body(), Epic[].class)));

        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void handleSubTasksListQueryTest() throws IOException, InterruptedException {
        Epic epic = addEpic();
        SubTask subTask = addSubtask(LocalDateTime.now(), epic.getId());
        URI uri = URI.create(taskServerUrl + "/tasks/subtask");
        HttpRequest request;
        HttpResponse<String> response;

        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(List.of(subTask), List.of(gson.fromJson(response.body(), SubTask[].class)));

        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());

        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    public void getEpicSubTasksListTest() throws IOException, InterruptedException {
        Epic epic = addEpic();
        SubTask subTask = addSubtask(LocalDateTime.now(), epic.getId());

        URI uri = URI.create(taskServerUrl + "/tasks/subtask/epic/?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(List.of(subTask), List.of(gson.fromJson(response.body(), SubTask[].class)));
    }

    @Test
    public void shouldReturn405ForWrongMethod() throws IOException, InterruptedException {
        URI uri = URI.create(taskServerUrl + "/tasks/task");
        HttpRequest request;
        HttpResponse<String> response;

        request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(taskServerUrl + "/tasks/epic");

        request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(taskServerUrl + "/tasks/subtask");

        request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    public void handleTaskQueryTest() throws IOException, InterruptedException {
        String context = "/tasks/task/?id=";
        URI uri;
        HttpRequest request;
        HttpResponse<String> response;

        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);
        task.setId(idCounter);
        String serTask = gson.toJson(task);

        uri = URI.create(taskServerUrl + context + task.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(serTask, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(201, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(taskServerUrl + context + task.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(task, gson.fromJson(response.body(), Task.class));

        uri = URI.create(taskServerUrl + context + task.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(taskServerUrl + context + task.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    public void handleEpicQueryTest() throws IOException, InterruptedException {
        String context = "/tasks/epic/?id=";
        URI uri;
        HttpRequest request;
        HttpResponse<String> response;

        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        epic.setId(idCounter);
        String serTask = gson.toJson(epic);

        uri = URI.create(taskServerUrl + context + epic.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(serTask, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(201, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(taskServerUrl + context + epic.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(epic, gson.fromJson(response.body(), Epic.class));

        uri = URI.create(taskServerUrl + context + epic.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(taskServerUrl + context + epic.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    public void handleSubTaskQueryTest() throws IOException, InterruptedException {
        String context = "/tasks/subtask/?id=";
        URI uri;
        HttpRequest request;
        HttpResponse<String> response;

        Epic epic = addEpic();

        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());
        subTask.setId(idCounter);

        String serTask = gson.toJson(subTask);

        uri = URI.create(taskServerUrl + context + subTask.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(serTask, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(201, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(taskServerUrl + context + subTask.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(subTask, gson.fromJson(response.body(), SubTask.class));

        uri = URI.create(taskServerUrl + context + subTask.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertTrue(response.body().isEmpty());

        uri = URI.create(taskServerUrl + context + subTask.getId());
        request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertTrue(response.body().isEmpty());
    }

    @Test
    public void getHistoryTest() throws IOException, InterruptedException {
        String context = "/tasks/history";
        Task task = addTask(LocalDateTime.now());
        Epic epic = addEpic();
        SubTask subTask = addSubtask(task.getEndTime().plusDays(1), epic.getId());

        epic.setStatus(subTask.getStatus());
        epic.setEpicStartTime(subTask.getStartTime());
        epic.setEpicDuration(subTask.getDuration());
        epic.assignNewSubtask(subTask.getId(), subTask.getStatus());

        getTask(TasksTypes.TASK, task.getId());
        getTask(TasksTypes.EPIC, epic.getId());
        getTask(TasksTypes.SUBTASK, subTask.getId());

        URI uri = URI.create(taskServerUrl + context);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        JsonElement jElement = JsonParser.parseString(response.body());
        JsonArray jArray = jElement.getAsJsonArray();

        assertEquals(task, gson.fromJson(jArray.get(0), Task.class));
        assertEquals(epic, gson.fromJson(jArray.get(1), Epic.class));
        assertEquals(subTask, gson.fromJson(jArray.get(2), SubTask.class));
    }

    @Test
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        String context = "/tasks/priority";
        Task task1 = addTask(LocalDateTime.now());
        Task task2 = addTask(task1.getEndTime().plusDays(30));
        Task task3 = addTask(task1.getEndTime().plusDays(10));

        URI uri = URI.create(taskServerUrl + context);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));

        assertEquals(List.of(task1, task3, task2), List.of(gson.fromJson(response.body(), Task[].class)));
    }

    @Test
    public void shouldReturnCorrectErrorCodesForBadTaskQueries() throws IOException, InterruptedException {
        String taskContext = "/tasks/task/?id=";
        String epicContext = "/tasks/epic/?id=";
        String subTaskContext = "/tasks/subtask/?id=";
        String getHistoryContext = "/tasks/history";
        String getPrioritizedTasksContext = "/tasks/priority";

        testBadQuery(taskContext);
        testBadQuery(epicContext);
        testBadQuery(subTaskContext);
        testBadQuery(getHistoryContext);
        testBadQuery(getPrioritizedTasksContext);
    }

    private Task addTask(LocalDateTime startTime) throws IOException, InterruptedException {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                startTime,
                4 * 24 * 60);
        task.setId(idCounter);
        String serTask = gson.toJson(task);

        URI uri = URI.create(taskServerUrl + "/tasks/task/?id=" + idCounter++);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(serTask, UTF_8))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        return task;
    }

    private Epic addEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        epic.setId(idCounter);
        String serTask = gson.toJson(epic);

        URI uri = URI.create(taskServerUrl + "/tasks/epic/?id=" + idCounter++);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(serTask, UTF_8))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        return epic;
    }

    private SubTask addSubtask(LocalDateTime startTime, int parentEpicId) throws IOException, InterruptedException {
        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                startTime,
                4 * 24 * 60,
                parentEpicId);
        subTask.setId(idCounter);
        String serTask = gson.toJson(subTask);

        URI uri = URI.create(taskServerUrl + "/tasks/subtask/?id=" + idCounter++);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(serTask, UTF_8))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        return subTask;
    }

    private Task getTask(TasksTypes type, int taskId) throws IOException, InterruptedException {
        URI uri;
        HttpRequest request;
        HttpResponse<String> response;

        switch (type.toString()) {
            case ("TASK"):
                uri = URI.create(taskServerUrl + "/tasks/task/?id=" + taskId);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
                return gson.fromJson(response.body(), Task.class);

            case ("EPIC"):
                uri = URI.create(taskServerUrl + "/tasks/epic/?id=" + taskId);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
                return gson.fromJson(response.body(), Epic.class);

            case ("SUBTASK"):
                uri = URI.create(taskServerUrl + "/tasks/subtask/?id=" + taskId);
                request = HttpRequest.newBuilder()
                        .uri(uri)
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
                return gson.fromJson(response.body(), SubTask.class);

            default:
                return null;
        }
    }

    private void testBadQuery(String context) throws IOException, InterruptedException {
        URI uri;
        HttpRequest request;
        HttpResponse<String> response;

        if (context.contains("id")) {
            uri = URI.create(taskServerUrl + context + 0);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
            assertEquals(404, response.statusCode());
        } else {
            uri = URI.create(taskServerUrl + context);
        }

        request = HttpRequest.newBuilder()
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
    }
}