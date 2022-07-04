package managers.filebacked;

import managers.*;
import managers.exceptions.ManagerSaveException;
import managers.exceptions.ParentEpicNotPresentException;
import managers.exceptions.TaskOutOfPlannerBoundsException;
import managers.exceptions.WrongTaskIdException;
import tasks.*;
import managers.inmemory.InMemoryTasksManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager {

    private final static String TASK_FIELDS = "id,type,name,status,description,epic,startTime,duration"
            + System.lineSeparator();
    private final File storage;

    public FileBackedTasksManager(String filePath) {
        this.storage = new File(filePath);
    }

    public FileBackedTasksManager(File file) {
        this.storage = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        int idCounter = 0;

        try {
            String data = Files.readString(file.toPath());
            String[] lines = data.split(System.lineSeparator());
            int taskLinesLength = lines.length - 2;
            List<Integer> historyList;

            for (int i = 1; i < taskLinesLength; i++) {
                Task newTask = FileBackedTasksManager.taskFromString(lines[i]);
                int newTaskId = newTask.getId();

                if (newTask instanceof Epic) {
                    manager.epicsMap.put(newTaskId, (Epic) newTask);

                } else if (newTask instanceof SubTask) {
                    SubTask subTask = (SubTask) newTask;
                    int parentEpicId = subTask.getParentEpicId();

                    manager.subTasksMap.put(newTaskId, subTask);
                    manager.epicsMap.get(parentEpicId).assignNewSubtask(newTaskId, subTask.getStatus());
                    manager.refreshEpicStatusAndTime(parentEpicId);

                } else {
                    manager.tasksMap.put(newTaskId, newTask);
                }

                if (newTaskId > idCounter) {
                    idCounter = newTaskId;
                }
            }
            manager.setIdCounter(idCounter + 1);

            if (lines.length > 0) {
                historyList = FileBackedTasksManager.historyFromString(lines[lines.length - 1]);

                for (int taskId : historyList) {
                    if (manager.epicsMap.containsKey(taskId)) {
                        manager.historyManager.add(manager.epicsMap.get(taskId));

                    } else if (manager.subTasksMap.containsKey(taskId)) {
                        manager.historyManager.add(manager.subTasksMap.get(taskId));

                    } else {
                        manager.historyManager.add(manager.tasksMap.get(taskId));
                    }
                }
            }
            return manager;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла: " + e.getMessage(), e);
        }
    }

    public static String taskToString(Task task, TasksTypes type) {
        String taskLine;

        if (type != TasksTypes.EPIC) {
            taskLine = String.join(",",
                    task.getId().toString(),
                    type.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    task.getStartTime().toString(),
                    task.getDuration().toString());

        } else {
            taskLine = String.join(",",
                    task.getId().toString(),
                    type.toString(),
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription());
        }

        if (type == TasksTypes.SUBTASK) {
            taskLine = taskLine + "," + ((SubTask) task).getParentEpicId();
        }
        return taskLine + System.lineSeparator();
    }

    public static Task taskFromString(String taskLine) {
        String[] taskData = taskLine.split(",");
        Task newTask;

        switch (taskData[1]) {
            case "EPIC":
                newTask = new Epic(
                        taskData[2],
                        taskData[4]);

                break;
            case "SUBTASK":
                newTask = new SubTask(taskData[2],
                        taskData[4],
                        TasksStatuses.valueOf(taskData[3]),
                        LocalDateTime.parse(taskData[5]),
                        Integer.parseInt(taskData[6]),
                        Integer.parseInt(taskData[7]));
                break;
            default:
                newTask = new Task(taskData[2],
                        taskData[4],
                        TasksStatuses.valueOf(taskData[3]),
                        LocalDateTime.parse(taskData[5]),
                        Integer.parseInt(taskData[6]));
        }
        newTask.setId(Integer.parseInt(taskData[0]));

        return newTask;
    }

    public static String historyToString(HistoryManager historyManager) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Task> history = historyManager.getHistory();

        if (!history.isEmpty()) {
            for (Task task : history) {
                stringBuilder.append(task.getId()).append(",");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        } else {
            stringBuilder.append("NULL");
        }

        return stringBuilder.toString();
    }

    public static List<Integer> historyFromString(String historyLine) {
        if (!historyLine.contains("NULL")) {
            String[] ids = historyLine.split(",");
            List<Integer> idsParsed = new ArrayList<>();

            for (String id : ids) {
                idsParsed.add(Integer.parseInt(id));
            }
            return idsParsed;

        } else return new ArrayList<>();
    }

    public void setIdCounter(int idCounter) {
        this.idCounter = idCounter;
    }

    @Override
    public void clearTasksMap() {
        super.clearTasksMap();
        save();
    }

    @Override
    public void clearEpicsMap() {
        super.clearEpicsMap();
        save();
    }

    @Override
    public void clearSubTasksMap() {
        super.clearSubTasksMap();
        save();
    }

    @Override
    public Task getTask(int id) throws WrongTaskIdException {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) throws WrongTaskIdException {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) throws WrongTaskIdException {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public void addNewTask(Task task) throws TaskOutOfPlannerBoundsException {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubTask(SubTask subTask) throws ParentEpicNotPresentException, TaskOutOfPlannerBoundsException {
        super.addNewSubTask(subTask);
        save();
    }

    @Override
    public void replaceTask(int id, Task task) throws WrongTaskIdException, TaskOutOfPlannerBoundsException {
        super.replaceTask(id, task);
        save();
    }

    @Override
    public void replaceEpic(int id, Epic epic) throws WrongTaskIdException, TaskOutOfPlannerBoundsException {
        super.replaceEpic(id, epic);
        save();
    }

    @Override
    public void replaceSubTask(int id, SubTask subTask) throws ParentEpicNotPresentException,
            WrongTaskIdException,
            TaskOutOfPlannerBoundsException {
        super.replaceSubTask(id, subTask);
        save();
    }

    @Override
    public void removeTask(int taskId) throws WrongTaskIdException {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) throws WrongTaskIdException {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubTask(int subTaskId) throws WrongTaskIdException {
        super.removeSubTask(subTaskId);
        save();
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(storage)) {
            StringBuilder stringBuilder = new StringBuilder(TASK_FIELDS);
            String taskLine;

            for (Epic epic : epicsMap.values()) {
                taskLine = FileBackedTasksManager.taskToString(epic, TasksTypes.EPIC);
                stringBuilder.append(taskLine);
            }

            for (SubTask subTask : subTasksMap.values()) {
                taskLine = FileBackedTasksManager.taskToString(subTask, TasksTypes.SUBTASK);
                stringBuilder.append(taskLine);
            }

            for (Task task : tasksMap.values()) {
                taskLine = FileBackedTasksManager.taskToString(task, TasksTypes.TASK);
                stringBuilder.append(taskLine);
            }
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(FileBackedTasksManager.historyToString(historyManager));

            fileWriter.write(stringBuilder.toString());

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла: " + e.getMessage(), e);
        }
    }
}
