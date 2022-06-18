package managers.filebacked;

import managers.HistoryManager;
import managers.Managers;
import tasks.TasksTypes;
import managers.inmemory.InMemoryTasksManager;
import managers.TasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TasksStatuses;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTasksManager {

    private final String TASK_FIELDS = "id,type,name,status,description,epic" + System.lineSeparator();
    private final File storage;

    public FileBackedTasksManager(String filePath) {
        this.storage = new File(filePath);
    }

    public FileBackedTasksManager(File file) {
        this.storage = file;
    }

    public static class Main {

        public static final String FILE_PATH = "src/files/Tasks.csv";
        public static final TasksManager manager = Managers.getFileBackedTasks(FILE_PATH);

        public static void main(String[] args) {
            TasksManager fileBackedManager;

            makeNewTestTasks();
            manager.getTask(1);
            manager.getEpic(3);
            manager.getSubTask(4);

            System.out.println("Начальный список тасков: ");
            printTaskList(manager.getTasksList());
            System.out.println("-------------------------");

            System.out.println("Начальный список эпиков: ");
            printTaskList(manager.getEpicsList());
            System.out.println("-------------------------");

            System.out.println("Начальный список сабтасков: ");
            printTaskList(manager.getSubTasksList());
            System.out.println("-------------------------");

            System.out.println("Начальная история: ");
            printTaskList(manager.getHistory());
            System.out.println("-------------------------");

            fileBackedManager = FileBackedTasksManager.loadFromFile(new File(FILE_PATH));

            System.out.println("Восстановленный список тасков: ");
            printTaskList(fileBackedManager.getTasksList());
            System.out.println("-------------------------");

            System.out.println("Восстановленный список эпиков: ");
            printTaskList(fileBackedManager.getEpicsList());
            System.out.println("-------------------------");

            System.out.println("Восстановленный список сабтасков: ");
            printTaskList(fileBackedManager.getSubTasksList());
            System.out.println("-------------------------");

            System.out.println("Восстановленная история: ");
            printTaskList(fileBackedManager.getHistory());
        }

        public static void makeNewTestTasks() {
            Task task;
            Epic epicTask;
            SubTask subTask;

            // id 1
            task = new Task("Thinking in Java", "Прочитать теорию",
                    TasksStatuses.IN_PROGRESS);
            manager.addNewTask(task);

            // id 2
            task = new Task("C++", "Начать изучать", TasksStatuses.NEW);
            manager.addNewTask(task);

            // id 3
            epicTask = new Epic("Третий спринт", "Закрыть ТЗ третьего спринта");
            manager.addNewEpic(epicTask);

            // id 4
            subTask = new SubTask("Тестовые данные", "Сделать тестовые задачи для трекера",
                    TasksStatuses.DONE, epicTask.getId());
            manager.addNewSubTask(subTask);

            // id 5
            subTask = new SubTask("Тест функций", "Оно работает?",
                    TasksStatuses.IN_PROGRESS, epicTask.getId());
            manager.addNewSubTask(subTask);

            // id 6
            epicTask = new Epic("Конец модуля", "Закрыть последний спринт");
            manager.addNewEpic(epicTask);

            // id 7
            subTask = new SubTask("Теоретическая часть", "Начать делать тренажер",
                    TasksStatuses.NEW, epicTask.getId());
            manager.addNewSubTask(subTask);

        }

        public static void printTaskList(List<? extends Task> list) {

            for (Task printableTask : list) {
                System.out.println(printableTask);
                System.out.println();
            }
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        try {
            String data = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            String[] lines = data.split(System.lineSeparator());
            int taskLinesLength = lines.length - 2;
            List<Integer> historyList;

            for (int i = 1; i < taskLinesLength; i++) {
                Task newTask = FileBackedTasksManager.taskFromString(lines[i]);

                if (newTask instanceof Epic) {
                    manager.epicsList.put(newTask.getId(), (Epic) newTask);

                } else if (newTask instanceof SubTask) {
                    SubTask subTask = (SubTask) newTask;
                    int subTaskId = subTask.getId();
                    int parentEpicId = subTask.getParentEpicId();

                    manager.subTasksList.put(subTaskId, subTask);
                    manager.epicsList.get(parentEpicId).assignNewSubtask(subTaskId, subTask.getStatus());
                    manager.refreshEpicStatus(parentEpicId);

                } else {
                    manager.tasksList.put(newTask.getId(), newTask);
                }
            }

            if (lines.length > 0) {
                historyList = FileBackedTasksManager.historyFromString(lines[lines.length - 1]);

                for (int taskId : historyList) {
                    if (manager.epicsList.containsKey(taskId)) {
                        manager.historyManager.add(manager.epicsList.get(taskId));

                    } else if (manager.subTasksList.containsKey(taskId)) {
                        manager.historyManager.add(manager.subTasksList.get(taskId));

                    } else {
                        manager.historyManager.add(manager.tasksList.get(taskId));
                    }
                }
            }
            return manager;

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла: " + e.getMessage(), e);
        }
    }

    public static String taskToString(Task task, TasksTypes type) {
        String taskLine = String.join(",",
                task.getId().toString(),
                type.toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription());

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
                newTask = new Epic(taskData[2],
                        taskData[4]);

                break;
            case "SUBTASK":
                newTask = new SubTask(taskData[2],
                        taskData[4],
                        TasksStatuses.valueOf(taskData[3]),
                        Integer.parseInt(taskData[5]));
                break;
            default:
                newTask = new Task(taskData[2],
                        taskData[4],
                        TasksStatuses.valueOf(taskData[3]));
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
        }

        return stringBuilder.toString();
    }

    public static List<Integer> historyFromString(String historyLine) {
        String[] ids = historyLine.split(",");
        List<Integer> idsParsed = new ArrayList<>();

        for (String id : ids) {
            idsParsed.add(Integer.parseInt(id));
        }
        return idsParsed;
    }

    @Override
    public void clearTasksList() {
        super.clearTasksList();
        save();
    }

    @Override
    public void clearEpicsList() {
        super.clearEpicsList();
        save();
    }

    @Override
    public void clearSubTasksList() {
        super.clearSubTasksList();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubTask(SubTask subTask) {
        super.addNewSubTask(subTask);
        save();
    }

    @Override
    public void replaceTask(int id, Task task) {
        super.replaceTask(id, task);
        save();
    }

    @Override
    public void replaceEpic(int id, Epic epic) {
        super.replaceEpic(id, epic);
        save();
    }

    @Override
    public void replaceSubTask(int id, SubTask subTask) {
        super.replaceSubTask(id, subTask);
        save();
    }

    @Override
    public void removeTask(int taskId) {
        super.removeTask(taskId);
        save();
    }

    @Override
    public void removeEpic(int epicId) {
        super.removeEpic(epicId);
        save();
    }

    @Override
    public void removeSubTask(int subTaskId) {
        super.removeSubTask(subTaskId);
        save();
    }

    private void save() {
        try (FileWriter fileWriter = new FileWriter(storage, StandardCharsets.UTF_8)) {
            StringBuilder stringBuilder = new StringBuilder(TASK_FIELDS);
            String taskLine;

            for (Epic epic : epicsList.values()) {
                taskLine = FileBackedTasksManager.taskToString(epic, TasksTypes.EPIC);
                stringBuilder.append(taskLine);
            }

            for (SubTask subTask : subTasksList.values()) {
                taskLine = FileBackedTasksManager.taskToString(subTask, TasksTypes.SUBTASK);
                stringBuilder.append(taskLine);
            }

            for (Task task : tasksList.values()) {
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
