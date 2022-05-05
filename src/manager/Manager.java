package manager;

import tasks.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Manager {

    private final Map<Integer, Task> tasksList = new HashMap<>();
    private final Map<Integer, Epic> epicsList = new HashMap<>();
    private final Map<Integer, SubTask> subTasksList = new HashMap<>();
    private int idCounter = 1;

    private int assignNewId() {
        return idCounter++;
    }

    public List<Task> getTasksList() {
        return new ArrayList<>(tasksList.values());
    }

    public List<Epic> getEpicsList() {
        return new ArrayList<>(epicsList.values());
    }

    public List<SubTask> getSubTasksList() {
        return new ArrayList<>(subTasksList.values());
    }

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

    public void clearTasksList() {
        tasksList.clear();
    }

    public void clearEpicsList() {
        epicsList.clear();
        subTasksList.clear();
    }

    public void clearSubTasksList() {
        subTasksList.clear();

        for (int epicId : epicsList.keySet()) {
            refreshEpicStatus(epicId);
        }
    }

    public Task getTaskById(int id) {
        return tasksList.get(id);
    }

    public Epic getEpicById(int id) {
        return epicsList.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTasksList.get(id);
    }

    public void addNewTask(Task task) {
        if (task != null) {
            if (task.getId() == null) {
                task.setId(assignNewId());
            }
            tasksList.put(task.getId(), task);
        }
    }

    public void addNewEpic(Epic epic) {
        if (epic != null) {
            if (epic.getId() == null) {
                epic.setId(assignNewId());
            }
            epicsList.put(epic.getId(), epic);
        }
    }

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

    public void replaceTask(int id, Task task) {
        task.setId(id);
        addNewTask(task);
    }

    public void replaceEpic(int id, Epic epic) {
        epic.setId(id);
        addNewEpic(epic);
    }

    public void replaceSubTask(int id, SubTask subTask) {
        subTask.setId(id);
        addNewSubTask(subTask);
    }

    public void removeTask(int taskId) {
        tasksList.remove(taskId);
    }

    public void removeEpic(int epicId) {
        Epic epic = epicsList.get(epicId);

        if (epic != null) {
            for (int subTaskId : epic.getSubTasksIds()) {
                subTasksList.remove(subTaskId);
            }
            epicsList.remove(epicId);
        }
    }

    public void removeSubTask(int subTaskId) {
        SubTask subTask = subTasksList.get(subTaskId);

        if (subTask != null) {
            int parentEpicId = subTask.getParentEpicId();

            subTasksList.remove(subTaskId);
            epicsList.get(parentEpicId).removeSubTask(subTaskId);
            refreshEpicStatus(parentEpicId);
        }
    }

    private void refreshEpicStatus(int parentEpicId) {
        int subTasksNumber = epicsList.get(parentEpicId).getSubTasksIds().size();
        int unfinishedSubTasksNumber = epicsList.get(parentEpicId).getUnfinishedTasksIds().size();

        if (subTasksNumber == unfinishedSubTasksNumber) {
            epicsList.get(parentEpicId).setStatus("NEW");

        } else if (unfinishedSubTasksNumber > 0) {
            epicsList.get(parentEpicId).setStatus("IN_PROGRESS");

        } else {
            epicsList.get(parentEpicId).setStatus("DONE");
        }
    }
}
