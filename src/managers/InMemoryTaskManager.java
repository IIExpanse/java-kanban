package managers;

import tasks.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Map<Integer, Task> tasksList = new HashMap<>();
    private final Map<Integer, Epic> epicsList = new HashMap<>();
    private final Map<Integer, SubTask> subTasksList = new HashMap<>();
    private int idCounter = 1;

    private int assignNewId() {
        return idCounter++;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epicsList.values());
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasksList.values());
    }

    @Override
    public List<SubTask> getEpicSubTasksList(int epicId) {
        Epic epic = epicsList.get(epicId);
        List<SubTask> specificEpicSubtasks = new ArrayList<>();

        if (epic != null) {
            for (int subTaskId : epic.getSubTasksIds()) {
                specificEpicSubtasks.add(subTasksList.get(subTaskId));
            }
        }
        return specificEpicSubtasks;
    }

    @Override
    public void clearTasksList() {
        tasksList.clear();
    }

    @Override
    public void clearEpicsList() {
        epicsList.clear();
        subTasksList.clear();
    }

    @Override
    public void clearSubTasksList() {
        subTasksList.clear();

        for (int epicId : epicsList.keySet()) {
            refreshEpicStatus(epicId);
        }
    }

    @Override
    public Task getTask(int id) {
        Task task = tasksList.get(id);

        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicsList.get(id);

        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
       SubTask subTask = subTasksList.get(id);

        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void addNewTask(Task task) {
        if (task != null) {
            if (task.getId() == null) {
                task.setId(assignNewId());
            }
            tasksList.put(task.getId(), task);
        }
    }

    @Override
    public void addNewEpic(Epic epic) {
        if (epic != null) {
            if (epic.getId() == null) {
                epic.setId(assignNewId());
            }
            epicsList.put(epic.getId(), epic);
        }
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        if (subTask != null) {
            if (subTask.getId() == null) {
                subTask.setId(assignNewId());
            }
            int parentEpicId = subTask.getParentEpicId();

            subTasksList.put(subTask.getId(), subTask);
            epicsList.get(parentEpicId).assignNewSubtask(subTask.getId(), subTask.getStatus());
            refreshEpicStatus(parentEpicId);
        }
    }

    @Override
    public void replaceTask(int id, Task task) {
        task.setId(id);
        addNewTask(task);
    }

    @Override
    public void replaceEpic(int id, Epic epic) {
        epic.setId(id);
        addNewEpic(epic);
    }

    @Override
    public void replaceSubTask(int id, SubTask subTask) {
        subTask.setId(id);
        addNewSubTask(subTask);
    }

    @Override
    public void removeTask(int taskId) {
        tasksList.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void removeEpic(int epicId) {
        Epic epic = epicsList.get(epicId);

        if (epic != null) {
            for (int subTaskId : epic.getSubTasksIds()) {
                subTasksList.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            epicsList.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    @Override
    public void removeSubTask(int subTaskId) {
        SubTask subTask = subTasksList.get(subTaskId);

        if (subTask != null) {
            int parentEpicId = subTask.getParentEpicId();

            subTasksList.remove(subTaskId);
            historyManager.remove(subTaskId);
            epicsList.get(parentEpicId).removeSubTask(subTaskId);
            refreshEpicStatus(parentEpicId);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void refreshEpicStatus(int parentEpicId) {
        int subTasksNumber = epicsList.get(parentEpicId).getSubTasksIds().size();
        int unfinishedSubTasksNumber = epicsList.get(parentEpicId).getUnfinishedTasksIds().size();

        if (subTasksNumber == unfinishedSubTasksNumber) {
            epicsList.get(parentEpicId).setStatus(TaskStatuses.NEW);

        } else if (unfinishedSubTasksNumber > 0) {
            epicsList.get(parentEpicId).setStatus(TaskStatuses.IN_PROGRESS);

        } else {
            epicsList.get(parentEpicId).setStatus(TaskStatuses.DONE);
        }
    }
}
