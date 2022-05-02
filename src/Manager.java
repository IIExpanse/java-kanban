import Tasks.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class Manager {

    public static Map<Integer, Task> generalTasksList = new HashMap<>();
    public static Map<Integer, Epic> epicsList = new HashMap<>();
    public static Map<Integer, SubTask> subTasksList = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Тестовые задачи {
        Task task;
        Epic epicTask;
        SubTask subTask;

        task = new Task("Thinking in Java", "Прочитать теорию", "IN PROGRESS");
        integrateNewTask(task);

        task = new Task("C++", "Начать изучать", "NEW");
        integrateNewTask(task);


        epicTask = new Epic("Третий спринт", "Закрыть ТЗ третьего спринта");
        integrateNewTask(epicTask);

        subTask = new SubTask("Тестовые данные", "Сделать тестовые задачи для трекера",
                "DONE", epicTask.getId());
        integrateNewTask(subTask);

        subTask = new SubTask("Тест функций", "Оно работает?", "IN PROGRESS", epicTask.getId());
        integrateNewTask(subTask);


        epicTask = new Epic("Конец модуля", "Закрыть последний спринт");
        integrateNewTask(epicTask);

        subTask = new SubTask("Теоретическая часть", "Начать делать тренажер", "NEW",
                epicTask.getId());
        integrateNewTask(subTask);
        // } Тестовые задачи


        String userInput = "-1";

        while (!userInput.equals("0")) {

            printMenu();
            userInput = scanner.nextLine().trim();

            switch (userInput) {
                case "1":
                    printTaskList(getTasksList(chooseTasksType(scanner)));

                    break;
                case "2":
                    if (epicsList.isEmpty()) {
                        System.out.println("Список эпиков пуст.");
                        System.out.println();
                        break;
                    }
                    getEpicSubTasks(pickYourEpic(scanner));

                    break;
                case "3":
                    clearTasksList(chooseTasksType(scanner));

                    break;
                case "4":
                    Map<Integer, ? extends Task> relevantTaskList = getTasksList(chooseTasksType(scanner));

                    if (relevantTaskList.isEmpty()) {
                        System.out.println("Нет ни одной задачи данного типа.");
                        System.out.println();
                        break;
                    }

                    System.out.println("Введите идентификатор задачи, которую вы хотите получить:");
                    printTaskList(relevantTaskList);
                    Task targetTask = relevantTaskList.get(enterTaskId(scanner));

                    if (targetTask == null) {
                        System.out.println("Задачи с таким идентификатором не существует.");
                        System.out.println();
                    } else {
                        System.out.println(targetTask);
                        System.out.println();
                    }

                    break;
                case "5":
                    Task newTask = constructNewTask(scanner, null);
                    // Конструктор задач для тестирования.
                    // Логика добавления готовых задач вынесена в integrateNewTask.
                    integrateNewTask(newTask);

                    System.out.println("Создана новая задача:");
                    System.out.println(newTask);
                    System.out.println();

                    break;
                case "6":
                    replaceTask(scanner);

                    break;
                case "7":
                    removeTask(scanner);
            }
        }
    }

    private static void printMenu() {
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Получить список всех задач");
        System.out.println("2 - Получить список подзадач эпика");
        System.out.println("3 - Удалить все задачи");
        System.out.println("4 - Получить задачу по идентификатору");
        System.out.println("5 - Создать новую задачу");
        System.out.println("6 - Обновить существующую задачу");
        System.out.println("7 - Удалить задачу по идентификатору");
        System.out.println("0 - Выйти из приложения");
    }

    private static String chooseTasksType(Scanner scanner) {
        String input;
        String taskType = null;

        while (taskType == null) {
            System.out.println("Выберите тип:");
            System.out.println("1 - Обычные задачи");
            System.out.println("2 - Эпики");
            System.out.println("3 - Подзадачи");
            input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    taskType = "Task";

                    break;
                case "2":
                    taskType = "Epic";

                    break;
                case "3":
                    taskType = "SubTask";

                    break;
                default:
                    System.out.println("Пожалуйста, выберите один из вариантов.");
                    System.out.println();
            }
        }
        return taskType;
    }

    private static Map<Integer, ? extends Task> getTasksList(String tasksType) {
        Map<Integer, ? extends Task> map = null;

        switch (tasksType) {
            case "Task":
                map = generalTasksList;

                break;
            case "Epic":
                map = epicsList;

                break;
            case "SubTask":
                map = subTasksList;

                break;
        }
        return map;
    }

    public static void getEpicSubTasks(int epicId) {
        Epic targetEpic = epicsList.get(epicId);

        if (targetEpic.getSubTasksIds().isEmpty()) {
            System.out.println("У данного эпика нет подзадач.");
            System.out.println();
            return;
        }

        System.out.println("Список подзадач эпика:");
        for (int subTaskId : targetEpic.getSubTasksIds()) {
            System.out.println(subTasksList.get(subTaskId));
            System.out.println();
        }
    }

    private static void clearTasksList(String tasksType) {
        if (getTasksList(tasksType).isEmpty()) {
            System.out.println("Список задач пуст.");
            System.out.println();
            return;
        }

        switch (tasksType) {
            case "Task":
                generalTasksList.clear();
                System.out.println("Задачи удалены.");
                System.out.println();

                break;
            case "Epic":
                epicsList.clear();
                subTasksList.clear();
                System.out.println("Эпики и подзадачи удалены.");
                System.out.println();

                break;
            case "SubTask":
                subTasksList.clear();
                System.out.println("Подзадачи удалены.");
                System.out.println();

                for (int epicId : epicsList.keySet()) {
                    epicsList.get(epicId).removeAllSubtasks();
                }

                break;
        }
    }

    private static int enterTaskId(Scanner scanner) {

        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите идентификатор в числовом формате.");
            }
        }
    }

    private static Task constructNewTask(Scanner scanner, String taskType) {
        String title;
        String description;
        Task newTask = null;

        System.out.println("Введите название задачи:");
        title = scanner.nextLine();

        System.out.println("Введите описание задачи:");
        description = scanner.nextLine();

        if (taskType == null) {
            taskType = chooseTasksType(scanner);
        }

        switch (taskType) {
            case "Task":
                newTask = new Task(title, description, pickTaskStatus(scanner));

                break;
            case "Epic":
                newTask = new Epic(title, description);

                break;
            case "SubTask":
                int parentEpicId;

                if (epicsList.isEmpty()) {
                    makeNewEpic(scanner);
                }

                parentEpicId = pickYourEpic(scanner);

                newTask = new SubTask(title, description, pickTaskStatus(scanner), parentEpicId);
        }
        return newTask;
    }

    public static void integrateNewTask(Task newTask) {
        String taskType = newTask.getClass().toString();

        switch (taskType) {
            case "class Tasks.Task":
                generalTasksList.put(newTask.getId(), newTask);

                break;
            case "class Tasks.Epic":
                epicsList.put(newTask.getId(), (Epic) newTask);

                break;
            case "class Tasks.SubTask":
                subTasksList.put(newTask.getId(), (SubTask) newTask);
                epicsList.get(((SubTask) newTask).getParentEpicId())
                        .assignNewSubtask(newTask.getId(), newTask.getStatus());
                // Получение эпика и добавление в него информации о новой присвоенной ему подзадаче.
        }
    }

    public static String pickTaskStatus(Scanner scanner) {

        while (true) {
            System.out.println("Выберите статус задачи");
            System.out.println("1 - NEW");
            System.out.println("2 - IN PROGRESS");
            System.out.println("3 - DONE");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    return "NEW";

                case "2":
                    return "IN PROGRESS";

                case "3":
                    return "DONE";

                default:
                    System.out.println("Пожалуйста, выберите один из вариантов.");
                    System.out.println();
            }
        }
    }

    public static int pickYourEpic(Scanner scanner) {
        int parentEpicId;

        System.out.println("Введите идентификатор эпика, к которому относится подзадача:");
        printTaskList(epicsList);
        parentEpicId = enterTaskId(scanner);

        while (!epicsList.containsKey(parentEpicId)) {
            System.out.println("Эпика с таким идентификатором нет."
                    + " Пожалуйста, введите идентификатор одного из существующих эпиков.");
            printTaskList(epicsList);
            parentEpicId = enterTaskId(scanner);
        }

        return parentEpicId;
    }

    public static void makeNewEpic(Scanner scanner) {
        System.out.println("Список эпиков пуст. Перед добавлением подзадачи необходимо создать эпик.");

        System.out.println("Введите название эпика:");
        String title = scanner.nextLine();

        System.out.println("Введите описание эпика:");
        String description = scanner.nextLine();

        Epic newEpic = new Epic(title, description);
        epicsList.put(newEpic.getId(), newEpic);
    }

    public static void replaceTask(Scanner scanner) {
        String taskType = chooseTasksType(scanner);
        Map<Integer, ? extends Task> relevantTaskList = getTasksList(taskType);

        if (relevantTaskList.isEmpty()) {
            System.out.println("Нет ни одной задачи данного типа.");
            System.out.println();
            return;
        }

        Task newTask = constructNewTask(scanner, taskType);
        int targetId;

        System.out.println("Введите идентификатор заменяемой задачи:");
        printTaskList(relevantTaskList);
        targetId = enterTaskId(scanner);

        while (!relevantTaskList.containsKey(targetId)) {
            System.out.println("Задачи с таким идентификатором нет."
                    + " Пожалуйста, введите идентификатор существующей задачи.");
            printTaskList(relevantTaskList);
            targetId = enterTaskId(scanner);
        }

        newTask.setId(targetId);
        integrateNewTask(newTask);

        System.out.println("Задача заменена.");
        System.out.println();
    }

    public static void removeTask(Scanner scanner) {
        String taskType = chooseTasksType(scanner);
        Map<Integer, ? extends Task> relevantTaskList = getTasksList(taskType);
        int targetId;

        if (relevantTaskList.isEmpty()) {
            System.out.println("Нет ни одной задачи данного типа.");
            System.out.println();
            return;
        }

        System.out.println("Введите идентификатор удаляемой задачи:");
        printTaskList(relevantTaskList);
        targetId = enterTaskId(scanner);

        while (!relevantTaskList.containsKey(targetId)) {
            System.out.println("Задачи с таким идентификатором нет."
                    + " Пожалуйста, введите идентификатор существующей задачи.");
            printTaskList(relevantTaskList);
            targetId = enterTaskId(scanner);
        }

        switch (taskType) {
            case "Task":
                generalTasksList.remove(targetId);

                break;
            case "Epic":
                for (Integer includedSubTaskId : epicsList.get(targetId).getSubTasksIds()) {
                    subTasksList.remove(includedSubTaskId);
                }
                epicsList.remove(targetId);

                break;
            case "SubTask":
                int parentEpicId = subTasksList.get(targetId).getParentEpicId();
                epicsList.get(parentEpicId).removeSubTask(targetId);
                subTasksList.remove(targetId);
        }
        System.out.println("Задача удалена.");
        System.out.println();
    }

    public static void printTaskList(Map<Integer, ? extends Task> map) {
        if (map.isEmpty()) {
            System.out.println("Список задач пуст.");
            System.out.println();
            return;
        }

        for (Task printableTask : map.values()) {
            System.out.println(printableTask);
            System.out.println();
        }
    }
}
