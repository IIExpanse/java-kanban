package managers.inmemory;

import managers.*;
import managers.exceptions.ParentEpicNotPresentException;
import managers.exceptions.TaskOutOfPlannerBoundsException;
import managers.exceptions.WrongTaskIdException;
import tasks.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class InMemoryTasksManager implements TasksManager {

    protected final Map<Integer, Task> tasksMap = new HashMap<>();
    protected final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected final Map<Integer, SubTask> subTasksMap = new HashMap<>();
    protected final HashMap<LocalDateTime, Boolean> tasksPlanner = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> tasksFilteredByTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int idCounter = 1;

    protected final int TASKS_PLANNER_INTERVAL_SIZE_MINUTES = 15;
    protected final int PLANNER_TIME_LIMIT_MINUTES = 60 * 24 * 365;

    public InMemoryTasksManager() {
        LocalDateTime startingInterval = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        startingInterval = startingInterval.withMinute(startingInterval.getMinute()
                / TASKS_PLANNER_INTERVAL_SIZE_MINUTES
                * TASKS_PLANNER_INTERVAL_SIZE_MINUTES);

        int intervalsAmount = PLANNER_TIME_LIMIT_MINUTES / TASKS_PLANNER_INTERVAL_SIZE_MINUTES;

        for (int i = 0; i < intervalsAmount; i++) {
            tasksPlanner.put(startingInterval, false);
            startingInterval = startingInterval.plusMinutes(TASKS_PLANNER_INTERVAL_SIZE_MINUTES);
        }
    }

    @Override
    public List<Task> getTasksList() {
        return List.copyOf(tasksMap.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return List.copyOf(epicsMap.values());
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return List.copyOf(subTasksMap.values());
    }

    @Override
    public List<SubTask> getEpicSubTasksList(int epicId) {
        Epic epic = epicsMap.get(epicId);
        List<SubTask> specificEpicSubtasks = new ArrayList<>();

        if (epic != null) {
            for (int subTaskId : epic.getSubTasksIds()) {
                specificEpicSubtasks.add(subTasksMap.get(subTaskId));
            }
        }
        return List.copyOf(specificEpicSubtasks);
    }

    @Override
    public void clearTasksMap() {
        for (Task task : tasksMap.values()) {
            tasksFilteredByTime.remove(task);
            historyManager.remove(task.getId());
        }
        tasksMap.clear();
    }

    @Override
    public void clearEpicsMap() {
        for (Epic epic : epicsMap.values()) {
            historyManager.remove(epic.getId());
        }
        epicsMap.clear();
        clearSubTasksMap();
    }

    @Override
    public void clearSubTasksMap() {
        for (SubTask subTask : subTasksMap.values()) {
            int subTaskId = subTask.getId();
            int parentEpicId = subTask.getParentEpicId();

            if (epicsMap.containsKey(parentEpicId)) {
                epicsMap.get(parentEpicId).removeSubTask(subTaskId);
                refreshEpicStatusAndTime(parentEpicId);
            }
            tasksFilteredByTime.remove(subTask);
            historyManager.remove(subTaskId);
        }
        subTasksMap.clear();
    }

    @Override
    public Task getTask(int id) throws WrongTaskIdException {
        Task task = tasksMap.get(id);

        if (task == null) {
            throw new WrongTaskIdException("Задача с Id '" + id + "' не найдена при получении.");
        }

        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) throws WrongTaskIdException {
        Epic epic = epicsMap.get(id);

        if (epic == null) {
            throw new WrongTaskIdException("Эпик с Id '" + id + "' не найден при получении.");
        }

        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) throws WrongTaskIdException {
        SubTask subTask = subTasksMap.get(id);

        if (subTask == null) {
            throw new WrongTaskIdException("Подзадача с Id '" + id + "' не найдена при получении.");
        }

        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void addNewTask(Task task) throws TaskOutOfPlannerBoundsException {
        if (task != null && isTaskNotOverlapping(task)) {
            if (task.getId() == null) {
                task.setId(assignNewId());
            }
            tasksMap.put(task.getId(), task);
            tasksFilteredByTime.add(task);
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        if (epic != null) {
            if (epic.getId() == null) {
                epic.setId(assignNewId());
            }
            epicsMap.put(epic.getId(), epic);
        }
    }

    @Override
    public void addNewSubTask(SubTask subTask) throws ParentEpicNotPresentException, TaskOutOfPlannerBoundsException {
        if (subTask != null && isTaskNotOverlapping(subTask)) {
            int parentEpicId = subTask.getParentEpicId();

            if (!epicsMap.containsKey(parentEpicId)) {
                throw new ParentEpicNotPresentException("Эпик с id '" + parentEpicId + "' не найден.");
            }

            if (subTask.getId() == null) {
                subTask.setId(assignNewId());
            }

            subTasksMap.put(subTask.getId(), subTask);
            epicsMap.get(parentEpicId).assignNewSubtask(subTask.getId(), subTask.getStatus());
            refreshEpicStatusAndTime(parentEpicId);
            tasksFilteredByTime.add(subTask);
        }
    }

    @Override
    public void replaceTask(int id, Task task) throws WrongTaskIdException, TaskOutOfPlannerBoundsException {
        if (task != null) {

            if (!tasksMap.containsKey(id)) {
                throw new WrongTaskIdException("Задача с id '" + id + "' не найдена при замене.");
            }

            task.setId(id);
            freeTimeIntervals(task);
            addNewTask(task);
        }
    }

    @Override
    public void replaceEpic(int id, Epic epic) throws WrongTaskIdException, TaskOutOfPlannerBoundsException {
        if (epic != null) {

            if (!epicsMap.containsKey(id)) {
                throw new WrongTaskIdException("Эпик с id '" + id + "' не найден при замене.");
            }

            epic.setId(id);
            addNewEpic(epic);
        }
    }

    @Override
    public void replaceSubTask(int id, SubTask subTask) throws ParentEpicNotPresentException,
            WrongTaskIdException,
            TaskOutOfPlannerBoundsException {
        if (subTask != null) {

            if (!subTasksMap.containsKey(id)) {
                throw new WrongTaskIdException("Подзадача с id '" + id + "' не найдена при замене.");
            }

            subTask.setId(id);
            freeTimeIntervals(subTask);
            addNewSubTask(subTask);
        }
    }

    @Override
    public void removeTask(int taskId) throws WrongTaskIdException {
        if (tasksMap.containsKey(taskId)) {
            Task task = tasksMap.get(taskId);

            tasksFilteredByTime.remove(task);
            freeTimeIntervals(task);
            tasksMap.remove(taskId);
            historyManager.remove(taskId);

        } else throw new WrongTaskIdException("Задача с id '" + taskId + "' не найдена при удалении.");
    }

    @Override
    public void removeEpic(int epicId) throws WrongTaskIdException {
        Epic epic = epicsMap.get(epicId);

        if (epic == null) {
            throw new WrongTaskIdException("Эпик с id '" + epicId + "' не найден при удалении.");
        }

        for (int subTaskId : epic.getSubTasksIds()) {
            subTasksMap.remove(subTaskId);
            historyManager.remove(subTaskId);
        }
        epicsMap.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void removeSubTask(int subTaskId) throws WrongTaskIdException {
        SubTask subTask = subTasksMap.get(subTaskId);

        if (subTask == null) {
            throw new WrongTaskIdException("Подзадача с id '" + subTaskId + "' не найдена при удалении.");
        }

        int parentEpicId = subTask.getParentEpicId();

        tasksFilteredByTime.remove(subTask);
        freeTimeIntervals(subTask);
        subTasksMap.remove(subTaskId);
        historyManager.remove(subTaskId);
        epicsMap.get(parentEpicId).removeSubTask(subTaskId);
        refreshEpicStatusAndTime(parentEpicId);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>((TreeSet<Task>) tasksFilteredByTime);
    }

    protected void refreshEpicStatusAndTime(int parentEpicId) {
        Epic epic = epicsMap.get(parentEpicId);
        List<Integer> subTasksIds = epic.getSubTasksIds();
        int subTasksNumber = subTasksIds.size();
        int unfinishedSubTasksNumber = epic.getUnfinishedTasksIds().size();

        if (subTasksNumber == unfinishedSubTasksNumber) {
            boolean containsInProgressSubTasks = subTasksIds.stream()
                    .map(subTasksMap::get)
                    .map(SubTask::getStatus)
                    .filter(Objects::nonNull)
                    .anyMatch(status -> status == TasksStatuses.IN_PROGRESS);

            if (containsInProgressSubTasks) {
                epic.setStatus(TasksStatuses.IN_PROGRESS);
            } else {
                epic.setStatus(TasksStatuses.NEW);
            }

        } else if (unfinishedSubTasksNumber > 0) {
            epic.setStatus(TasksStatuses.IN_PROGRESS);

        } else {
            epic.setStatus(TasksStatuses.DONE);
        }

        if (subTasksNumber != 0) {
            Task initialTask = subTasksMap.get(subTasksIds.get(0));
            LocalDateTime startTime = initialTask.getStartTime();
            int duration = initialTask.getDuration();

            for (int i = 1; i < subTasksNumber; i++) {
                Task nextTask = subTasksMap.get(subTasksIds.get(i));
                LocalDateTime nextTime = nextTask.getStartTime();
                duration += nextTask.getDuration();

                if (nextTime.isBefore(startTime)) {
                    startTime = nextTime;
                }
            }

            epic.setEpicStartTime(startTime);
            epic.setEpicDuration(duration);

        } else {
            epic.setEpicStartTime(null);
            epic.setEpicDuration(null);
        }
    }

    protected int assignNewId() {
        return idCounter++;
    }

    protected boolean isTaskNotOverlapping(Task task) throws TaskOutOfPlannerBoundsException {
        LocalDateTime startingInterval = task.getStartTime().truncatedTo(ChronoUnit.MINUTES);
        startingInterval = startingInterval.withMinute(startingInterval.getMinute()
                / TASKS_PLANNER_INTERVAL_SIZE_MINUTES
                * TASKS_PLANNER_INTERVAL_SIZE_MINUTES);

        int intervalsNumber = task.getDuration() / TASKS_PLANNER_INTERVAL_SIZE_MINUTES;

        if (tasksPlanner.containsKey(startingInterval)
                && tasksPlanner.containsKey(startingInterval
                .plusMinutes(TASKS_PLANNER_INTERVAL_SIZE_MINUTES * intervalsNumber))) {

            for (int i = 0; i < intervalsNumber; i++) {
                boolean isIntervalOccupied = tasksPlanner.get(startingInterval);

                if (!isIntervalOccupied) {
                    tasksPlanner.put(startingInterval, true);
                    startingInterval = startingInterval.plusMinutes(TASKS_PLANNER_INTERVAL_SIZE_MINUTES);

                } else {
                    return false;
                }
            }
            return true;

        } else {
            throw new TaskOutOfPlannerBoundsException("Время начала и/или окончания задачи не входит в диапазон " +
                    "планировщика задач (до года с настоящего момента).");
        }
    }

    protected void freeTimeIntervals(Task task) {
        LocalDateTime startingInterval = task.getStartTime().truncatedTo(ChronoUnit.MINUTES);
        startingInterval = startingInterval.withMinute(startingInterval.getMinute()
                / TASKS_PLANNER_INTERVAL_SIZE_MINUTES
                * TASKS_PLANNER_INTERVAL_SIZE_MINUTES);

        int intervalsNumber = task.getDuration() / TASKS_PLANNER_INTERVAL_SIZE_MINUTES;

        for (int i = 0; i < intervalsNumber; i++) {
            tasksPlanner.put(startingInterval, false);
            startingInterval = startingInterval.plusMinutes(TASKS_PLANNER_INTERVAL_SIZE_MINUTES);
        }
    }
}