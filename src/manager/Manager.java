package manager;

import tasks.*;

import java.util.Map;
import java.util.HashMap;

public class Manager {

    private final Map<Integer, Task> generalTasksList = new HashMap<>();
    private final Map<Integer, Epic> epicsList = new HashMap<>();
    private final Map<Integer, SubTask> subTasksList = new HashMap<>();
    private int idCounter = 1;

    private int assignNewId() {
        return idCounter++;
    }

    public Map<Integer, Task> getGeneralTasksList() {
        return generalTasksList;
    }

    public Map<Integer, Epic> getEpicsList() {
        return epicsList;
    }

    public Map<Integer, SubTask> getSubTasksList() {
        return subTasksList;
    }

    public Map<Integer, SubTask> getEpicSubTasksList(int epicId) {
        Epic epic = epicsList.get(epicId);
        Map<Integer, SubTask> specificEpicSubtasks = new HashMap<>();

        if (epic != null) {
            for (int subTaskId : epic.getSubTasksIds()) {
                specificEpicSubtasks.put(subTaskId, subTasksList.get(subTaskId));
            }
        }
        return specificEpicSubtasks;
    }

    public void clearRegularTasksList() {
        generalTasksList.clear();
    }

    public void clearEpicTasksList() {
        epicsList.clear();
        subTasksList.clear();
    }

    public void clearSubTasksList() {
        subTasksList.clear();

        for (int epicId : epicsList.keySet()) {
            refreshEpicStatus(epicId);
        }
    }

    public Task getRegularTaskById(int id) {
        return getGeneralTasksList().get(id);
    }

    public Epic getEpicTaskById(int id) {
        return getEpicsList().get(id);
    }

    public SubTask getSubTaskById(int id) {
        return getSubTasksList().get(id);
    }

    public void addNewTask(Task newTask) {
        if (newTask != null) {
            if (newTask.getId() == null) {
                newTask.setId(assignNewId());
            }

            if (newTask instanceof SubTask) {
                int parentEpicId = ((SubTask) newTask).getParentEpicId();

                subTasksList.put(newTask.getId(), (SubTask) newTask);
                epicsList.get(parentEpicId).assignNewSubtask(newTask.getId(), newTask.getStatus());
                refreshEpicStatus(parentEpicId);

            } else if (newTask instanceof Epic) {
                epicsList.put(newTask.getId(), (Epic) newTask);

            } else {
                generalTasksList.put(newTask.getId(), newTask);
            }
        }
    }

    public void replaceTask(int id, Task newTask) {
        newTask.setId(id);
        addNewTask(newTask);
    }

    public void removeRegularTask(int taskId) {
        generalTasksList.remove(taskId);
    }

    public void removeEpicTask(int epicId) {
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

    public void refreshEpicStatus(int parentEpicId) {
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
