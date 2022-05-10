package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<SubTask> getSubTasksList();

    List<SubTask> getEpicSubTasksList(int epicId);

    void clearTasksList();

    void clearEpicsList();

    void clearSubTasksList();

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addNewSubTask(SubTask subTask);

    void replaceTask(int id, Task task);

    void replaceEpic(int id, Epic epic);

    void replaceSubTask(int id, SubTask subTask);

    void removeTask(int taskId);

    void removeEpic(int epicId);

    void removeSubTask(int subTaskId);

    List<Task> getHistory();
}
