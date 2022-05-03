package main;

import manager.Manager;
import tasks.*;

import java.util.Map;
import java.util.Scanner;

public class Main {

    public static final Manager manager = new Manager();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Task task;
        Epic epicTask;
        SubTask subTask;

        task = new Task("Thinking in Java", "Прочитать теорию",
                "IN_PROGRESS", manager.assignNewId());
        manager.integrateNewTask(task);

        task = new Task("C++", "Начать изучать", "NEW", manager.assignNewId());
        manager.integrateNewTask(task);


        epicTask = new Epic("Третий спринт", "Закрыть ТЗ третьего спринта", manager.assignNewId());
        manager.integrateNewTask(epicTask);

        subTask = new SubTask("Тестовые данные", "Сделать тестовые задачи для трекера",
                "DONE", manager.assignNewId(), epicTask.getId());
        manager.integrateNewTask(subTask);

        subTask = new SubTask("Тест функций", "Оно работает?",
                "IN_PROGRESS", manager.assignNewId(), epicTask.getId());
        manager.integrateNewTask(subTask);


        epicTask = new Epic("Конец модуля", "Закрыть последний спринт", manager.assignNewId());
        manager.integrateNewTask(epicTask);

        subTask = new SubTask("Теоретическая часть", "Начать делать тренажер",
                "NEW", manager.assignNewId(), epicTask.getId());
        manager.integrateNewTask(subTask);


        String userInput = "-1";

        while (!userInput.equals("0")) {

            printMenu();
            userInput = scanner.nextLine().trim();

            switch (userInput) {
                case "1":
                    printTaskList(manager.getTasksList(chooseTasksType(scanner)));

                    break;
                case "2":
                    if (manager.epicsList.isEmpty()) {
                        System.out.println("Список эпиков пуст.");
                        System.out.println();
                        break;
                    }
                    manager.getEpicSubTasks(pickYourEpic(scanner));

                    break;
                case "3":
                    manager.clearTasksList(chooseTasksType(scanner));

                    break;
                case "4":
                    Map<Integer, ? extends Task> relevantTaskList = manager.getTasksList(chooseTasksType(scanner));

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
                    manager.integrateNewTask(newTask);

                    System.out.println("Создана новая задача:");
                    System.out.println(newTask);
                    System.out.println();

                    break;
                case "6":
                    manager.replaceTask(scanner);

                    break;
                case "7":
                    manager.removeTask(scanner);
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

    public static String chooseTasksType(Scanner scanner) {
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

    public static Task constructNewTask(Scanner scanner, String taskType) {
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
                newTask = new Task(title, description, pickTaskStatus(scanner), manager.assignNewId());

                break;
            case "Epic":
                newTask = new Epic(title, description, manager.assignNewId());

                break;
            case "SubTask":
                int parentEpicId;

                if (manager.epicsList.isEmpty()) {
                    makeNewEpic(scanner);
                }

                parentEpicId = pickYourEpic(scanner);

                newTask = new SubTask(title, description, pickTaskStatus(scanner), manager.assignNewId(), parentEpicId);
        }
        return newTask;
    }

    public static String pickTaskStatus(Scanner scanner) {

        while (true) {
            System.out.println("Выберите статус задачи");
            System.out.println("1 - NEW");
            System.out.println("2 - IN_PROGRESS");
            System.out.println("3 - DONE");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    return "NEW";

                case "2":
                    return "IN_PROGRESS";

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
        printTaskList(manager.epicsList);
        parentEpicId = enterTaskId(scanner);

        while (!manager.epicsList.containsKey(parentEpicId)) {
            System.out.println("Эпика с таким идентификатором нет."
                    + " Пожалуйста, введите идентификатор одного из существующих эпиков.");
            printTaskList(manager.epicsList);
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

        Epic newEpic = new Epic(title, description, manager.assignNewId());
        manager.epicsList.put(newEpic.getId(), newEpic);
    }

    public static int enterTaskId(Scanner scanner) {

        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите идентификатор в числовом формате.");
            }
        }
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
