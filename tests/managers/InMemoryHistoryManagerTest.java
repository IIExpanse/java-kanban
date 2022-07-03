package managers;

import managers.inmemory.InMemoryTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TasksStatuses;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    TasksManager manager;

    @BeforeEach
    public void refreshManager() {
        manager = new InMemoryTasksManager();
    }

    @Test
    public void getHistoryTest() {
        assertEquals(List.of(), manager.getHistory());
    }

    @Test
    public void addTest() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                Duration.ofDays(4));
        try {
            manager.addNewTask(task);
            manager.getTask(task.getId());
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertTrue(manager.getHistory().contains(task));
    }

    @Test
    public void shouldNotAddSameTaskTwice() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                Duration.ofDays(4));
        try {
            manager.addNewTask(task);
            manager.getTask(task.getId());
            manager.getTask(task.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertEquals(1, manager.getHistory().size());
    }

    @Test
    public void removeTaskTest() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                Duration.ofDays(4));
        try {
            manager.addNewTask(task);
            manager.getTask(task.getId());
            manager.removeTask(task.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertTrue(manager.getHistory().isEmpty());
    }
}
