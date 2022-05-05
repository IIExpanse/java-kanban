package main;

import manager.Manager;
import tasks.*;

import java.util.List;

public class Main {

    public static final Manager manager = new Manager();

    public static void main(String[] args) {

        makeNewTestTasks();

        printTaskList(manager.getTasksList());
        printTaskList(manager.getEpicsList());
        printTaskList(manager.getSubTasksList());

        printTaskList(manager.getEpicSubTasksList(3));

        manager.clearTasksList();
        manager.clearEpicsList();
        manager.clearSubTasksList();
        makeNewTestTasks();

        printTask(manager.getTaskById(8));
        printTask(manager.getTaskById(1234));
        printTask(manager.getEpicById(10));
        printTask(manager.getEpicById(1234));
        printTask(manager.getSubTaskById(11));
        printTask(manager.getSubTaskById(1234));

        Task task = new Task("Task_title", "Task_description", "DONE");
        manager.addNewTask(task);
        Epic epic = new Epic("Epic_title", "Epic_description");
        manager.addNewEpic(epic);
        SubTask subTask = new SubTask("SubTask_title", "SubTask_description",
                "NEW", epic.getId());
        manager.addNewSubTask(subTask);

        manager.replaceTask(8, task);
        manager.replaceEpic(10, epic);
        manager.replaceSubTask(11, subTask);

        manager.removeTask(8);
        manager.removeEpic(10);
        manager.removeSubTask(11);

        printTaskList(manager.getEpicsList());
        SubTask newSubTask = new SubTask("New_title", "New_description", "DONE", 13);
        manager.replaceSubTask(14, newSubTask);
        printTaskList(manager.getEpicsList());
    }

    public static void makeNewTestTasks() {
        Task task;
        Epic epicTask;
        SubTask subTask;

        task = new Task("Thinking in Java", "Прочитать теорию",
                "IN_PROGRESS");
        manager.addNewTask(task);

        task = new Task("C++", "Начать изучать", "NEW");
        manager.addNewTask(task);


        epicTask = new Epic("Третий спринт", "Закрыть ТЗ третьего спринта");
        manager.addNewEpic(epicTask);

        subTask = new SubTask("Тестовые данные", "Сделать тестовые задачи для трекера",
                "DONE", epicTask.getId());
        manager.addNewSubTask(subTask);

        subTask = new SubTask("Тест функций", "Оно работает?",
                "IN_PROGRESS", epicTask.getId());
        manager.addNewSubTask(subTask);


        epicTask = new Epic("Конец модуля", "Закрыть последний спринт");
        manager.addNewEpic(epicTask);

        subTask = new SubTask("Теоретическая часть", "Начать делать тренажер",
                "NEW", epicTask.getId());
        manager.addNewSubTask(subTask);
    }

    public static void printTask(Task task) {
        System.out.println(task);
        System.out.println();
    }

    public static void printTaskList(List<? extends Task> list) {

        for (Task printableTask : list) {
            System.out.println(printableTask);
            System.out.println();
        }
    }
}
