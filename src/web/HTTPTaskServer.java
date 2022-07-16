package web;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.Managers;
import managers.TasksManager;
import managers.exceptions.ParentEpicNotPresentException;
import managers.exceptions.TaskOutOfPlannerBoundsException;
import managers.exceptions.WrongTaskIdException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HTTPTaskServer {

    public static final int PORT = 8080;
    private final HttpServer server;
    private final TasksManager manager;
    private final Gson gson = new Gson();

    public HTTPTaskServer(String serverUrl) throws IOException, InterruptedException {
        manager = Managers.getDefault(serverUrl);

        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::handleTasksListQuery);
        server.createContext("/tasks/task/", this::handleTaskQuery);
        server.createContext("/tasks/epic", this::handleEpicsListQuery);
        server.createContext("/tasks/epic/", this::handleEpicQuery);
        server.createContext("/tasks/subtask", this::handleSubTasksListQuery);
        server.createContext("/tasks/subtask/", this::handleSubTaskQuery);
        server.createContext("/tasks/subtask/epic/", this::getEpicSubTasksList);
        server.createContext("/tasks/history", this::getHistory);
        server.createContext("/tasks/priority", this::getPrioritizedTasks);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(1);
    }

    private void handleTasksListQuery(HttpExchange h) throws IOException {
        System.out.println("/tasks/task");
        try {
            switch (h.getRequestMethod()) {
                case "GET":
                    byte[] tasksList = gson.toJson(manager.getTasksList()).getBytes(UTF_8);
                    h.sendResponseHeaders(200, tasksList.length);

                    try (OutputStream os = h.getResponseBody()) {
                        os.write(tasksList);
                    }

                    break;
                case "DELETE":
                    manager.clearTasksMap();
                    h.sendResponseHeaders(200, -1);

                    break;
                default:
                    h.sendResponseHeaders(405, -1);
            }
        } finally {
            h.close();
        }
    }

    private void handleEpicsListQuery(HttpExchange h) throws IOException {
        System.out.println("/tasks/epic");
        try {
            switch (h.getRequestMethod()) {
                case "GET":
                    byte[] epicsList = gson.toJson(manager.getEpicsList()).getBytes(UTF_8);
                    h.sendResponseHeaders(200, epicsList.length);

                    try (OutputStream os = h.getResponseBody()) {
                        os.write(epicsList);
                    }

                    break;
                case "DELETE":
                    manager.clearEpicsMap();
                    h.sendResponseHeaders(200, -1);

                    break;
                default:
                    h.sendResponseHeaders(405, -1);
            }
        } finally {
            h.close();
        }
    }

    private void handleSubTasksListQuery(HttpExchange h) throws IOException {
        System.out.println("/tasks/subtask");
        try {
            switch (h.getRequestMethod()) {
                case "GET":
                    byte[] subTasksList = gson.toJson(manager.getSubTasksList()).getBytes(UTF_8);
                    h.sendResponseHeaders(200, subTasksList.length);

                    try (OutputStream os = h.getResponseBody()) {
                        os.write(subTasksList);
                    }

                    break;
                case "DELETE":
                    manager.clearSubTasksMap();
                    h.sendResponseHeaders(200, -1);

                    break;
                default:
                    h.sendResponseHeaders(405, -1);
            }
        } finally {
            h.close();
        }
    }

    private void getEpicSubTasksList(HttpExchange h) throws IOException {
        System.out.println("/tasks/subtask/epic/?id=");
        try {
            if ("GET".equals(h.getRequestMethod())) {
                int parsedId;
                byte[] subtasks;

                String id = h.getRequestURI().getQuery().split("=")[1];
                parsedId = Integer.parseInt(id);
                subtasks = gson.toJson(manager.getEpicSubTasksList(parsedId)).getBytes(UTF_8);

                h.sendResponseHeaders(200, subtasks.length);
                try (OutputStream os = h.getResponseBody()) {
                    os.write(subtasks);
                }

            } else {
                h.sendResponseHeaders(405, -1);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            h.sendResponseHeaders(400, -1);
        } catch (WrongTaskIdException e) {
            h.sendResponseHeaders(404, -1);
        } finally {
            h.close();
        }
    }

    private void handleTaskQuery(HttpExchange h) throws IOException {
        System.out.println("/tasks/task/?id=");
        try {
            int parsedId;
            String id;

            switch (h.getRequestMethod()) {
                case "GET":
                    byte[] taskBytes;

                    id = h.getRequestURI().getQuery().split("=")[1];
                    parsedId = Integer.parseInt(id);
                    taskBytes = gson.toJson(manager.getTask(parsedId)).getBytes(UTF_8);
                    h.sendResponseHeaders(200, taskBytes.length);

                    try (OutputStream os = h.getResponseBody()) {
                        os.write(taskBytes);
                    }

                    break;
                case "POST":
                    Task task;

                    id = h.getRequestURI().getQuery().split("=")[1];
                    parsedId = Integer.parseInt(id);
                    task = gson.fromJson(new String(h.getRequestBody().readAllBytes(), UTF_8), Task.class);
                    task.setId(parsedId);
                    manager.addNewTask(task);
                    h.sendResponseHeaders(201, -1);

                    break;
                case "DELETE":
                    id = h.getRequestURI().getQuery().split("=")[1];
                    parsedId = Integer.parseInt(id);
                    manager.removeTask(parsedId);
                    h.sendResponseHeaders(200, -1);

                    break;
                default:
                    h.sendResponseHeaders(405, -1);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            h.sendResponseHeaders(400, -1);
        } catch (WrongTaskIdException e) {
            h.sendResponseHeaders(404, -1);
        } catch (TaskOutOfPlannerBoundsException e) {
            h.sendResponseHeaders(409, -1);
        } finally {
            h.close();
        }
    }

    private void handleEpicQuery(HttpExchange h) throws IOException {
        System.out.println("/tasks/epic/?id=");
        try {
            int parsedId;
            String id;

            switch (h.getRequestMethod()) {
                case "GET":
                    byte[] epicBytes;

                    id = h.getRequestURI().getQuery().split("=")[1];
                    parsedId = Integer.parseInt(id);
                    epicBytes = gson.toJson(manager.getEpic(parsedId)).getBytes(UTF_8);
                    h.sendResponseHeaders(200, epicBytes.length);

                    try (OutputStream os = h.getResponseBody()) {
                        os.write(epicBytes);
                    }

                    break;
                case "POST":
                    try {
                        Epic epic;

                        id = h.getRequestURI().getQuery().split("=")[1];
                        parsedId = Integer.parseInt(id);
                        epic = gson.fromJson(new String(h.getRequestBody().readAllBytes(), UTF_8), Epic.class);
                        epic.setId(parsedId);
                        manager.addNewEpic(epic);
                        h.sendResponseHeaders(201, -1);

                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        h.sendResponseHeaders(400, -1);
                    }

                    break;
                case "DELETE":
                    id = h.getRequestURI().getQuery().split("=")[1];
                    parsedId = Integer.parseInt(id);
                    manager.removeEpic(parsedId);
                    h.sendResponseHeaders(200, -1);

                    break;
                default:
                    h.sendResponseHeaders(405, -1);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            h.sendResponseHeaders(400, -1);
        } catch (WrongTaskIdException e) {
            h.sendResponseHeaders(404, -1);
        } finally {
            h.close();
        }
    }

    private void handleSubTaskQuery(HttpExchange h) throws IOException {
        System.out.println("/tasks/subtask/?id=");
        try {
            String id;
            int parsedId;
            switch (h.getRequestMethod()) {
                case "GET":
                    byte[] subTaskBytes;

                    id = h.getRequestURI().getQuery().split("=")[1];
                    parsedId = Integer.parseInt(id);
                    subTaskBytes = gson.toJson(manager.getSubTask(parsedId)).getBytes(UTF_8);
                    h.sendResponseHeaders(200, subTaskBytes.length);

                    try (OutputStream os = h.getResponseBody()) {
                        os.write(subTaskBytes);
                    }

                    break;
                case "POST":
                    SubTask subTask;

                    id = h.getRequestURI().getQuery().split("=")[1];
                    parsedId = Integer.parseInt(id);
                    subTask = gson.fromJson(new String(h.getRequestBody().readAllBytes(), UTF_8), SubTask.class);
                    subTask.setId(parsedId);
                    manager.addNewSubTask(subTask);
                    h.sendResponseHeaders(201, -1);

                    break;
                case "DELETE":
                    id = h.getRequestURI().getQuery().split("=")[1];
                    parsedId = Integer.parseInt(id);
                    manager.removeSubTask(parsedId);
                    h.sendResponseHeaders(200, -1);


                    break;
                default:
                    h.sendResponseHeaders(405, -1);
            }
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            h.sendResponseHeaders(400, -1);
        } catch (WrongTaskIdException e) {
            h.sendResponseHeaders(404, -1);
        } catch (ParentEpicNotPresentException | TaskOutOfPlannerBoundsException e) {
            h.sendResponseHeaders(409, -1);
        } finally {
            h.close();
        }
    }

    private void getHistory(HttpExchange h) throws IOException {
        System.out.println("/tasks/history");
        try {
            if ("GET".equals(h.getRequestMethod())) {
                byte[] history = gson.toJson(manager.getHistory()).getBytes(UTF_8);
                h.sendResponseHeaders(200, history.length);

                try (OutputStream os = h.getResponseBody()) {
                    os.write(history);
                }

            } else {
                h.sendResponseHeaders(405, -1);
            }
        } finally {
            h.close();
        }
    }

    private void getPrioritizedTasks(HttpExchange h) throws IOException {
        System.out.println("/tasks/priority");
        try {
            if ("GET".equals(h.getRequestMethod())) {
                byte[] prioritizedTasks = gson.toJson(manager.getPrioritizedTasks()).getBytes(UTF_8);
                h.sendResponseHeaders(200, prioritizedTasks.length);

                try (OutputStream os = h.getResponseBody()) {
                    os.write(prioritizedTasks);
                }

            } else {
                h.sendResponseHeaders(405, -1);
            }
        } finally {
            h.close();
        }
    }
}
