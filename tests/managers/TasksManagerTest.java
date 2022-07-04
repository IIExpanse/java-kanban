package managers;

import managers.exceptions.ParentEpicNotPresentException;
import managers.exceptions.TaskOutOfPlannerBoundsException;
import managers.exceptions.WrongTaskIdException;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TasksManagerTest<T extends TasksManager> {

    public T manager;

    @Test
    public void getTasksListTest() {
        List<Task> list = List.of();

        assertEquals(list, manager.getTasksList());
    }

    @Test
    public void getEpicsListTest() {
        List<Epic> list = List.of();

        assertEquals(list, manager.getEpicsList());
    }

    @Test
    public void getSubTasksListTest() {
        List<SubTask> list = List.of();

        assertEquals(list, manager.getSubTasksList());
    }

    @Test
    public void getEpicSubTasksListTest() {
        List<SubTask> subTasksListOfEpic;

        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());

        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        subTasksListOfEpic = manager.getEpicSubTasksList(epic.getId());

        assertEquals(List.of(subTask), subTasksListOfEpic);
    }

    @Test
    public void shouldReturnEmptySubTasksListForEmptyEpic() {
        List<SubTask> subTasksListOfEpic;

        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        subTasksListOfEpic = manager.getEpicSubTasksList(epic.getId());

        assertEquals(List.of(), subTasksListOfEpic);
    }

    @Test
    public void clearTasksMapTest() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);

        try {
            manager.addNewTask(task);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        manager.clearTasksMap();

        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    public void clearEpicsMapTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);
        manager.clearEpicsMap();

        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    public void clearSubTasksMapTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());
        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        manager.clearSubTasksMap();

        assertTrue(manager.getSubTasksList().isEmpty());
    }

    @Test
    public void getTaskTest() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);

        try {
            manager.addNewTask(task);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        Task calledTask;
        try {
            calledTask = manager.getTask(task.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertEquals(task, calledTask);
    }

    @Test
    public void shouldThrowExceptionForWrongTaskId() {
        assertThrows(WrongTaskIdException.class, () -> manager.getTask(31));
    }

    @Test
    public void getEpicTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        Epic calledEpic;
        try {
            calledEpic = manager.getEpic(epic.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertEquals(epic, calledEpic);
    }

    @Test
    public void shouldThrowExceptionForWrongEpicId() {
        assertThrows(WrongTaskIdException.class, () -> manager.getEpic(31));
    }

    @Test
    public void getSubTaskTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());

        try {
            manager.addNewSubTask(subTask);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        SubTask calledSubTask;
        try {
            calledSubTask = manager.getSubTask(subTask.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertEquals(subTask, calledSubTask);
    }

    @Test
    public void shouldThrowExceptionForWrongSubTaskId() {
        assertThrows(WrongTaskIdException.class, () -> manager.getSubTask(31));
    }

    @Test
    public void addNewTaskTest() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);

        try {
            manager.addNewTask(task);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertTrue(manager.getTasksList().contains(task));
    }

    @Test
    public void shouldNotAddNullTask() {

        try {
            manager.addNewTask(null);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    public void addNewEpicTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        assertTrue(manager.getEpicsList().contains(epic));
    }

    @Test
    public void shouldNotAddNullEpic() {

        manager.addNewEpic(null);

        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    public void addNewSubTaskTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());
        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        assertTrue(manager.getSubTasksList().contains(subTask));
    }

    @Test
    public void shouldNotAddNullSubTask() {
        try {
            manager.addNewSubTask(null);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertTrue(manager.getSubTasksList().isEmpty());
    }

    @Test
    public void shouldThrowExceptionForAbsentParentEpicId() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                31);

        assertThrows(ParentEpicNotPresentException.class, () -> manager.addNewSubTask(subTask));
    }

    @Test
    public void replaceTaskTest() {
        Task task1 = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);

        try {
            manager.addNewTask(task1);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        Task task2 = new Task(
                "C++",
                "Начать изучать",
                TasksStatuses.NEW,
                task1.getEndTime().plus(Duration.ofDays(1)),
                4 * 24 * 60);

        try {
            manager.replaceTask(task1.getId(), task2);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        List<Task> list = manager.getTasksList();
        assertTrue(list.contains(task2) && !list.contains(task1));
    }

    @Test
    public void shouldNotReplaceWithNullTask() {
        Task task1 = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);

        try {
            manager.addNewTask(task1);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        try {
            manager.replaceTask(task1.getId(), null);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        List<Task> list = manager.getTasksList();
        assertTrue(list.contains(task1) && list.size() == 1);
    }

    @Test
    public void shouldNotReplaceWithWrongTaskId() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);

        assertThrows(WrongTaskIdException.class, () -> manager.replaceTask(31, task));
    }

    @Test
    public void replaceEpic() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        Epic epic2 = new Epic(
                "Конец модуля",
                "Закрыть последний спринт");

        try {
            manager.replaceEpic(epic.getId(), epic2);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        List<Epic> list = manager.getEpicsList();
        assertTrue(list.contains(epic2) && !list.contains(epic));
    }

    @Test
    public void shouldNotReplaceWithNullEpic() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        try {
            manager.replaceEpic(epic.getId(), null);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        List<Epic> list = manager.getEpicsList();
        assertTrue(list.contains(epic) && list.size() == 1);
    }

    @Test
    public void shouldNotReplaceWithWrongEpicId() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        Epic epic2 = new Epic(
                "Конец модуля",
                "Закрыть последний спринт");

        assertThrows(WrongTaskIdException.class, () -> manager.replaceEpic(31, epic2));
    }

    @Test
    public void replaceSubTaskTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask1 = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());

        SubTask subTask2 = new SubTask(
                "Теоретическая часть",
                "Начать делать тренажер",
                TasksStatuses.NEW,
                subTask1.getEndTime().plusDays(1),
                4 * 24 * 60,
                epic.getId());
        try {
            manager.addNewSubTask(subTask1);
            manager.replaceSubTask(subTask1.getId(), subTask2);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        List<SubTask> list = manager.getSubTasksList();
        assertTrue(list.contains(subTask2) && !list.contains(subTask1));
    }

    @Test
    public void shouldNotReplaceWithNullSubTask() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask1 = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());

        try {
            manager.addNewSubTask(subTask1);
            manager.replaceSubTask(subTask1.getId(), null);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        List<SubTask> list = manager.getSubTasksList();
        assertTrue(list.contains(subTask1) && list.size() == 1);
    }

    @Test
    public void shouldNotReplaceWithWrongSubTaskId() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask2 = new SubTask(
                "Теоретическая часть",
                "Начать делать тренажер",
                TasksStatuses.NEW,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());

        assertThrows(WrongTaskIdException.class, () -> manager.replaceSubTask(31, subTask2));
    }

    @Test
    public void removeTaskTest() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);

        try {
            manager.addNewTask(task);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        try {
            manager.removeTask(task.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    public void removeEpicTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        try {
            manager.removeEpic(epic.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    public void removeSubTaskTest() {
        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                LocalDateTime.now(),
                4 * 24 * 60,
                epic.getId());

        try {
            manager.addNewSubTask(subTask);
            manager.removeSubTask(subTask.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertTrue(manager.getSubTasksList().isEmpty());
    }

    @Test
    public void getHistoryTest() {
        Task task = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);

        try {
            manager.addNewTask(task);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        Epic epic = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epic);

        SubTask subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                task.getEndTime().plusDays(1),
                4 * 24 * 60,
                epic.getId());

        try {
            manager.addNewSubTask(subTask);
            manager.getTask(task.getId());
            manager.getEpic(epic.getId());
            manager.getSubTask(subTask.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        List<Task> list = manager.getHistory();

        assertTrue(list.contains(task)
                && list.indexOf(task) == 0
                && list.contains(epic)
                && list.indexOf(epic) == 1
                && list.contains(subTask)
                && list.indexOf(subTask) == 2);

        try {
            manager.removeTask(task.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        list = manager.getHistory();

        assertTrue(!list.contains(task)
                && list.contains(epic)
                && list.indexOf(epic) == 0
                && list.contains(subTask)
                && list.indexOf(subTask) == 1);
    }

    @Test
    public void getPrioritizedTasksTest() {

        Task task;
        Epic epicTask;
        SubTask subTask;
        Set<Task> sortedSet;
        LocalDateTime previousTaskEndTime = LocalDateTime.now();
        List<Task> correctlyOrderedList = new ArrayList<>();
        int mod;

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
        correctlyOrderedList.add(task);

        // id 2
        task = new Task(
                "C++",
                "Начать изучать",
                TasksStatuses.NEW,
                previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60);
        try {
            manager.addNewTask(task);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        previousTaskEndTime = task.getEndTime();
        correctlyOrderedList.add(task);

        // id 3
        epicTask = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epicTask);

        // id 4
        subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        previousTaskEndTime = subTask.getEndTime();
        correctlyOrderedList.add(subTask);

        // id 5
        subTask = new SubTask(
                "Тест функций",
                "Оно работает?",
                TasksStatuses.IN_PROGRESS,
                previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        previousTaskEndTime = subTask.getEndTime();
        correctlyOrderedList.add(subTask);

        // id 6
        epicTask = new Epic(
                "Конец модуля",
                "Закрыть последний спринт");
        manager.addNewEpic(epicTask);

        // id 7
        subTask = new SubTask(
                "Теоретическая часть",
                "Начать делать тренажер",
                TasksStatuses.NEW, previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        correctlyOrderedList.add(subTask);

        sortedSet = manager.getPrioritizedTasks();
        assertEquals(5, sortedSet.size());
        mod = 0;

        for (Task sortedTask : sortedSet) {
            if (!sortedTask.equals(correctlyOrderedList.get(mod))) {
                throw new AssertionError("Порядок задач в отсортированном по времени списке" +
                        " не соответствует ожидаемому.");
            }
            mod++;
        }


        try {
            manager.removeSubTask(subTask.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        sortedSet = manager.getPrioritizedTasks();
        assertEquals(4, sortedSet.size());
        mod = 0;

        for (Task sortedTask : sortedSet) {
            if (!sortedTask.equals(correctlyOrderedList.get(mod))) {
                throw new AssertionError("Порядок задач в отсортированном по времени списке" +
                        " не соответствует ожидаемому.");
            }
            mod++;
        }
    }

    @Test
    public void refreshEpicTimeTest() {
        Epic epicTask;
        SubTask subTask;
        Set<Task> sortedSet;
        LocalDateTime epicStartTime;
        Integer epicDuration;
        LocalDateTime previousTaskEndTime = LocalDateTime.now();

        epicTask = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epicTask);

        subTask = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        previousTaskEndTime = subTask.getEndTime();

        subTask = new SubTask(
                "Тест функций",
                "Оно работает?",
                TasksStatuses.IN_PROGRESS,
                previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        previousTaskEndTime = subTask.getEndTime();

        subTask = new SubTask(
                "Теоретическая часть",
                "Начать делать тренажер",
                TasksStatuses.NEW, previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        sortedSet = manager.getPrioritizedTasks();
        epicStartTime = subTask.getEndTime();
        epicDuration = 0;

        for (Task task : sortedSet) {
            LocalDateTime taskStartTime = task.getStartTime();

            if (epicStartTime.isAfter(taskStartTime)) {
                epicStartTime = taskStartTime;
            }
            epicDuration += task.getDuration();
        }

        assertTrue(epicStartTime.equals(epicTask.getStartTime())
                && epicDuration.equals(epicTask.getDuration()));


        try {
            manager.removeSubTask(subTask.getId());

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        sortedSet = manager.getPrioritizedTasks();
        epicStartTime = subTask.getEndTime();
        epicDuration = 0;

        for (Task task : sortedSet) {
            LocalDateTime taskStartTime = task.getStartTime();

            if (epicStartTime.isAfter(taskStartTime)) {
                epicStartTime = taskStartTime;
            }
            epicDuration += task.getDuration();
        }

        assertTrue(epicStartTime.equals(epicTask.getStartTime())
                && epicDuration.equals(epicTask.getDuration()));
    }

    @Test
    public void refreshEpicStatusTest() {
        Epic epicTask;
        LocalDateTime previousTaskEndTime = LocalDateTime.now();
        TasksStatuses newStatus;

        epicTask = new Epic(
                "Третий спринт",
                "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epicTask);

        SubTask subTask1 = new SubTask(
                "Тестовые данные",
                "Сделать тестовые задачи для трекера",
                TasksStatuses.DONE,
                previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask1);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        previousTaskEndTime = subTask1.getEndTime();

        SubTask subTask2 = new SubTask(
                "Тест функций",
                "Оно работает?",
                TasksStatuses.NEW,
                previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask2);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        previousTaskEndTime = subTask2.getEndTime();

        SubTask subTask3 = new SubTask(
                "Теоретическая часть",
                "Начать делать тренажер",
                TasksStatuses.NEW, previousTaskEndTime.plus(Duration.ofDays(1)),
                4 * 24 * 60,
                epicTask.getId());
        try {
            manager.addNewSubTask(subTask3);

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertEquals(TasksStatuses.IN_PROGRESS, epicTask.getStatus());

        newStatus = TasksStatuses.NEW;
        try {
            manager.replaceSubTask(subTask1.getId(), replaceSubTaskStatus(subTask1, newStatus));

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        assertEquals(TasksStatuses.NEW, epicTask.getStatus());


        newStatus = TasksStatuses.DONE;
        try {
            manager.replaceSubTask(subTask1.getId(), replaceSubTaskStatus(subTask1, newStatus));
            manager.replaceSubTask(subTask2.getId(), replaceSubTaskStatus(subTask2, newStatus));
            manager.replaceSubTask(subTask3.getId(), replaceSubTaskStatus(subTask3, newStatus));

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        assertEquals(TasksStatuses.DONE, epicTask.getStatus());


        newStatus = TasksStatuses.IN_PROGRESS;
        try {
            manager.replaceSubTask(subTask1.getId(), replaceSubTaskStatus(subTask1, newStatus));
            manager.replaceSubTask(subTask2.getId(), replaceSubTaskStatus(subTask2, newStatus));
            manager.replaceSubTask(subTask3.getId(), replaceSubTaskStatus(subTask3, newStatus));

        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }
        assertEquals(TasksStatuses.IN_PROGRESS, epicTask.getStatus());


        manager.clearSubTasksMap();
        assertEquals(TasksStatuses.NEW, epicTask.getStatus());
    }

    @Test
    public void shouldNotAddOverlappingTasks() {
        Task task1 = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now(),
                4 * 24 * 60);
        try {
            manager.addNewTask(task1);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        Task task2 = new Task(
                "C++",
                "Начать изучать",
                TasksStatuses.NEW,
                task1.getStartTime(),
                4 * 24 * 60);

        try {
            manager.addNewTask(task2);
        } catch (Exception e) {
            throw new AssertionError(e.getMessage(), e);
        }

        assertFalse(manager.getTasksList().contains(task2));
    }

    @Test
    public void shouldThrowExceptionForTasksPlanningMoreThanYearAhead() {
        Task task1 = new Task(
                "Thinking in Java",
                "Прочитать теорию",
                TasksStatuses.IN_PROGRESS,
                LocalDateTime.now().plusYears(2),
                4 * 24 * 60);

        assertThrows(TaskOutOfPlannerBoundsException.class, () -> manager.addNewTask(task1));
    }

    protected static SubTask replaceSubTaskStatus(SubTask subTask, TasksStatuses status) {
        return new SubTask(
                subTask.getName(),
                subTask.getDescription(),
                status,
                subTask.getStartTime(),
                subTask.getDuration(),
                subTask.getParentEpicId());
    }
}
