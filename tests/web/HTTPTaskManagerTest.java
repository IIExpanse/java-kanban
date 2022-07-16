package web;

import managers.http.HTTPTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TasksStatuses;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HTTPTaskManagerTest {
    private KVServer server;
    private HTTPTaskManager manager;
    private final String serverUrl = "http://localhost:" + KVServer.PORT;

    @BeforeEach
    public void startServer() throws IOException, InterruptedException {
        server = new KVServer();
        server.start();
        manager = new HTTPTaskManager(serverUrl);
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void testSaveAndLoad() throws IOException, InterruptedException {
        fillManager();
        HTTPTaskManager manager2 = new HTTPTaskManager(serverUrl);

        assertEquals(manager.getTasksList(), manager2.getTasksList());
        assertEquals(manager.getEpicsList(), manager2.getEpicsList());
        assertEquals(manager.getSubTasksList(), manager2.getSubTasksList());
        assertEquals(manager.getHistory(), manager2.getHistory());
        assertEquals(manager.getPrioritizedTasks(), manager2.getPrioritizedTasks());
    }

    private void fillManager() {
        try {
            Task task;
            Epic epicTask;
            SubTask subTask;
            LocalDateTime previousTaskEndTime = LocalDateTime.now();

            // id 1
            task = new Task(
                    "Thinking in Java",
                    "Прочитать теорию",
                    TasksStatuses.IN_PROGRESS,
                    previousTaskEndTime.plus(Duration.ofDays(1)),
                    4 * 24 * 60);
            manager.addNewTask(task);
            previousTaskEndTime = task.getEndTime();

            // id 2
            epicTask = new Epic(
                    "Третий спринт",
                    "Закрыть ТЗ третьего спринта");
            manager.addNewEpic(epicTask);

            // id 3
            subTask = new SubTask(
                    "Тестовые данные",
                    "Сделать тестовые задачи для трекера",
                    TasksStatuses.DONE,
                    previousTaskEndTime.plus(Duration.ofDays(1)),
                    4 * 24 * 60,
                    epicTask.getId());
            manager.addNewSubTask(subTask);
            manager.getTask(task.getId());
            manager.getEpic(epicTask.getId());
            manager.getSubTask(subTask.getId());
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }
}
