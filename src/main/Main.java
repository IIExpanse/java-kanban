package main;

import manager.Manager;
import tasks.*;

import java.util.Map;

public class Main {

    public static final Manager manager = new Manager();

    public static void main(String[] args) {

        makeNewTestTasks();

        printTaskList(manager.getGeneralTasksList());
        printTaskList(manager.getEpicsList());
        printTaskList(manager.getSubTasksList());

        printTaskList(manager.getEpicSubTasksList(3));

        manager.clearRegularTasksList();
        manager.clearEpicTasksList();
        manager.clearSubTasksList();
        makeNewTestTasks();

        printTask(manager.getRegularTaskById(8));
        printTask(manager.getRegularTaskById(1234));
        printTask(manager.getEpicTaskById(10));
        printTask(manager.getEpicTaskById(1234));
        printTask(manager.getSubTaskById(11));
        printTask(manager.getSubTaskById(1234));

        Task newTask = new Task("New_title", "New_description", "DONE");
        manager.addNewTask(newTask);

        manager.replaceTask(8, newTask);

        manager.removeRegularTask(8);
        manager.removeEpicTask(10);
        manager.removeSubTask(11);

        printTaskList(manager.getEpicsList());
        SubTask subTask = new SubTask("New_title", "New_description", "DONE", 13);
        manager.replaceTask(14, subTask);
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
        manager.addNewTask(epicTask);

        subTask = new SubTask("Тестовые данные", "Сделать тестовые задачи для трекера",
                "DONE", epicTask.getId());
        manager.addNewTask(subTask);

        subTask = new SubTask("Тест функций", "Оно работает?",
                "IN_PROGRESS", epicTask.getId());
        manager.addNewTask(subTask);


        epicTask = new Epic("Конец модуля", "Закрыть последний спринт");
        manager.addNewTask(epicTask);

        subTask = new SubTask("Теоретическая часть", "Начать делать тренажер",
                "NEW", epicTask.getId());
        manager.addNewTask(subTask);
    }

    public static void printTask(Task task) {
        System.out.println(task);
        System.out.println();
    }

    public static void printTaskList(Map<Integer, ? extends Task> map) {

        for (Task printableTask : map.values()) {
            System.out.println(printableTask);
            System.out.println();
        }
    }
}
