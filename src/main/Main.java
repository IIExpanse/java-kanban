package main;

import managers.Managers;
import managers.TaskManager;
import tasks.*;

import java.util.List;

public class Main {

    public static final TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {

        makeNewTestTasks();

        taskManager.getTask(1);
        System.out.println("Вызов истории из 1 задачи:");
        printTaskList(taskManager.getHistory());

        taskManager.getEpic(3);
        taskManager.getSubTask(4);
        System.out.println("-------------------------");
        System.out.println("Вызов истории из 3 задач:");
        printTaskList(taskManager.getHistory());

        taskManager.getTask(2);
        taskManager.getSubTask(5);
        taskManager.getSubTask(6);
        taskManager.getEpic(7);
        System.out.println("-------------------------");
        System.out.println("Вызов истории из 7 задач:");
        printTaskList(taskManager.getHistory());

        taskManager.getTask(1);
        System.out.println("-------------------------");
        System.out.println("Вызов истории из 7 задач, 1 (id 1) вызвана повторно:");
        printTaskList(taskManager.getHistory());

        taskManager.getEpic(7);
        taskManager.getSubTask(4);
        taskManager.getTask(1);
        System.out.println("-------------------------");
        System.out.println("Вызов истории из 7 задач, 3 (id 1,4,7) вызваны повторно:");
        printTaskList(taskManager.getHistory());

        taskManager.removeTask(1);
        System.out.println("-------------------------");
        System.out.println("Вызов истории из 6 задач, 1 (id 1) удалена:");
        printTaskList(taskManager.getHistory());

        taskManager.removeEpic(3);
        System.out.println("-------------------------");
        System.out.println("Вызов истории из 2 задач, 1 (id 3) эпик удален, " +
                            "\n 3 сабтаски (id 4, 5, 6) из эпика должны удалиться по умолчанию:");
        printTaskList(taskManager.getHistory());

        taskManager.removeEpic(31);
        System.out.println("-------------------------");
        System.out.println("Попытка удалить (в т.ч. из истории) несуществующую задачу:");
        printTaskList(taskManager.getHistory());
    }

    public static void makeNewTestTasks() {
        Task task;
        Epic epicTask;
        SubTask subTask;

        // id 1
        task = new Task("Thinking in Java", "Прочитать теорию",
                TaskStatuses.IN_PROGRESS);
        taskManager.addNewTask(task);

        // id 2
        task = new Task("C++", "Начать изучать", TaskStatuses.NEW);
        taskManager.addNewTask(task);

        // id 3
        epicTask = new Epic("Третий спринт", "Закрыть ТЗ третьего спринта");
        taskManager.addNewEpic(epicTask);

        // id 4
        subTask = new SubTask("Тестовые данные", "Сделать тестовые задачи для трекера",
                TaskStatuses.DONE, epicTask.getId());
        taskManager.addNewSubTask(subTask);

        // id 5
        subTask = new SubTask("Тест функций", "Оно работает?",
                TaskStatuses.IN_PROGRESS, epicTask.getId());
        taskManager.addNewSubTask(subTask);

        // id 6
        subTask = new SubTask("Теоретическая часть", "Начать делать тренажер",
                TaskStatuses.NEW, epicTask.getId());
        taskManager.addNewSubTask(subTask);

        // id 7
        epicTask = new Epic("Конец модуля", "Закрыть последний спринт");
        taskManager.addNewEpic(epicTask);

    }

    public static void printTaskList(List<? extends Task> list) {

        for (Task printableTask : list) {
            System.out.println(printableTask);
            System.out.println();
        }
    }
}
