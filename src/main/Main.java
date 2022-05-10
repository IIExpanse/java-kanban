package main;

import managers.Managers;
import managers.TaskManager;
import tasks.*;

import java.util.List;

public class Main {

    public static final TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {

        makeNewTestTasks();

        printTaskList(taskManager.getTasksList());
        printTaskList(taskManager.getEpicsList());
        printTaskList(taskManager.getSubTasksList());

        printTaskList(taskManager.getEpicSubTasksList(3));

        taskManager.clearTasksList();
        taskManager.clearEpicsList();
        taskManager.clearSubTasksList();
        makeNewTestTasks();

        printTask(taskManager.getTask(8));
        printTaskList(taskManager.getHistory());

        printTask(taskManager.getEpic(10));
        printTaskList(taskManager.getHistory());

        printTask(taskManager.getSubTask(11));
        printTaskList(taskManager.getHistory());

        Task task = new Task("Task_title", "Task_description", TaskStatuses.DONE);
        taskManager.addNewTask(task);
        Epic epic = new Epic("Epic_title", "Epic_description");
        taskManager.addNewEpic(epic);
        SubTask subTask = new SubTask("SubTask_title", "SubTask_description",
                TaskStatuses.NEW, epic.getId());
        taskManager.addNewSubTask(subTask);

        taskManager.replaceTask(8, task);
        taskManager.replaceEpic(10, epic);
        taskManager.replaceSubTask(11, subTask);

        taskManager.removeTask(8);
        taskManager.removeEpic(10);
        taskManager.removeSubTask(11);

        printTaskList(taskManager.getEpicsList());
        SubTask newSubTask = new SubTask("New_title", "New_description", TaskStatuses.DONE, 13);
        taskManager.replaceSubTask(14, newSubTask);
        printTaskList(taskManager.getEpicsList());
    }

    public static void makeNewTestTasks() {
        Task task;
        Epic epicTask;
        SubTask subTask;

        task = new Task("Thinking in Java", "Прочитать теорию",
                TaskStatuses.IN_PROGRESS);
        taskManager.addNewTask(task);

        task = new Task("C++", "Начать изучать", TaskStatuses.NEW);
        taskManager.addNewTask(task);


        epicTask = new Epic("Третий спринт", "Закрыть ТЗ третьего спринта");
        taskManager.addNewEpic(epicTask);

        subTask = new SubTask("Тестовые данные", "Сделать тестовые задачи для трекера",
                TaskStatuses.DONE, epicTask.getId());
        taskManager.addNewSubTask(subTask);

        subTask = new SubTask("Тест функций", "Оно работает?",
                TaskStatuses.IN_PROGRESS, epicTask.getId());
        taskManager.addNewSubTask(subTask);


        epicTask = new Epic("Конец модуля", "Закрыть последний спринт");
        taskManager.addNewEpic(epicTask);

        subTask = new SubTask("Теоретическая часть", "Начать делать тренажер",
                TaskStatuses.NEW, epicTask.getId());
        taskManager.addNewSubTask(subTask);
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
