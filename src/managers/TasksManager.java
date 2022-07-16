package managers;

import managers.exceptions.ParentEpicNotPresentException;
import managers.exceptions.TaskOutOfPlannerBoundsException;
import managers.exceptions.WrongTaskIdException;
import tasks.*;

import java.util.List;
import java.util.Set;

public interface TasksManager {
    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<SubTask> getSubTasksList();

    List<SubTask> getEpicSubTasksList(int epicId) throws WrongTaskIdException;

    void clearTasksMap();

    void clearEpicsMap();

    void clearSubTasksMap();

    Task getTask(int id) throws WrongTaskIdException;

    Epic getEpic(int id) throws WrongTaskIdException;

    SubTask getSubTask(int id) throws WrongTaskIdException;

    void addNewTask(Task task) throws TaskOutOfPlannerBoundsException;

    void addNewEpic(Epic epic);

    void addNewSubTask(SubTask subTask) throws ParentEpicNotPresentException, TaskOutOfPlannerBoundsException;

    void replaceTask(int id, Task task) throws WrongTaskIdException, TaskOutOfPlannerBoundsException;

    void replaceEpic(int id, Epic epic) throws WrongTaskIdException, TaskOutOfPlannerBoundsException;

    void replaceSubTask(int id, SubTask subTask) throws ParentEpicNotPresentException,
                                                        WrongTaskIdException,
                                                        TaskOutOfPlannerBoundsException;

    void removeTask(int taskId) throws WrongTaskIdException;

    void removeEpic(int epicId) throws WrongTaskIdException;

    void removeSubTask(int subTaskId) throws WrongTaskIdException;

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
