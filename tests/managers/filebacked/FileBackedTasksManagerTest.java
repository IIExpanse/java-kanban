package managers.filebacked;

import managers.TasksManagerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TasksStatuses;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TasksManagerTest<FileBackedTasksManager> {

    public static final String FILE_PATH = "src/files/Tasks.csv";

    @BeforeEach
    public void refreshManager() {
        manager = new FileBackedTasksManager(FILE_PATH);
    }

    @Test
    public void saveAndLoadManagerTest() {
        Task task;
        Epic epicTask;
        SubTask subTask;
        LocalDateTime previousTaskEndTime = LocalDateTime.now();
        List<Task> history1;
        List<Task> history2;
        FileBackedTasksManager manager2;

        // id 1
        task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60);
        try {
            manager.addNewTask(task);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
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
        try {
            manager.addNewSubTask(subTask);
            manager.getTask(task.getId());
            manager.getEpic(epicTask.getId());
            manager.getSubTask(subTask.getId());
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        history1 = manager.getHistory();

        manager2 = FileBackedTasksManager.loadFromFile(new File(FILE_PATH));
        history2 = manager2.getHistory();

        assertEquals(history1, history2);
        try {
            assertEquals(task, manager2.getTask(task.getId()));
            assertEquals(epicTask, manager2.getEpic(epicTask.getId()));
            assertEquals(subTask, manager2.getSubTask(subTask.getId()));
            assertEquals(manager.getTasksList(), manager2.getTasksList());
            assertEquals(manager.getEpicsList(), manager2.getEpicsList());
            assertEquals(manager.getSubTasksList(), manager2.getSubTasksList());
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
    }

    @Test
    public void saveAndLoadEmptyManagerTest() {
        FileBackedTasksManager.loadFromFile(new File(FILE_PATH));
    }

    @Test
    public void saveAndLoadManagerWithEmptyEpicAndHistory() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File(FILE_PATH));
        Epic epic2;

        try {
            epic2 = manager2.getEpic(epic.getId());
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        assertEquals(epic, epic2);
    }
}
