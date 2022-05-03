package manager;

import main.Main;
import tasks.*;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

public class Manager {

    public final Map<Integer, Task> generalTasksList = new HashMap<>();
    public final Map<Integer, Epic> epicsList = new HashMap<>();
    public final Map<Integer, SubTask> subTasksList = new HashMap<>();
    public int idCounter = 1;

    public int assignNewId() {
        return idCounter++;
    }

    public Map<Integer, ? extends Task> getTasksList(String tasksType) {
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
        }
        return map;
    }

    public void getEpicSubTasks(int epicId) {
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

    public void clearTasksList(String tasksType) {
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
                    epicsList.get(epicId).setStatus("NEW");
                }
        }
    }

    public void integrateNewTask(Task newTask) {
        if (newTask instanceof SubTask) {
            int parentEpicId = ((SubTask) newTask).getParentEpicId();

            subTasksList.put(newTask.getId(), (SubTask) newTask);
            epicsList.get(parentEpicId).assignNewSubtask(newTask.getId(), newTask.getStatus());
            refreshEpicStatus(parentEpicId);

        } else if (newTask instanceof Epic) {
            epicsList.put(newTask.getId(), (Epic) newTask);

        } else {
            generalTasksList.put(newTask.getId(), newTask);
        }
    }

    public void replaceTask(Scanner scanner) {
        String taskType = Main.chooseTasksType(scanner);
        Map<Integer, ? extends Task> relevantTaskList = getTasksList(taskType);
        int targetId;

        if (relevantTaskList.isEmpty()) {
            System.out.println("Нет ни одной задачи данного типа.");
            System.out.println();
            return;
        }

        Task newTask = Main.constructNewTask(scanner, taskType);

        System.out.println("Введите идентификатор заменяемой задачи:");
        Main.printTaskList(relevantTaskList);
        targetId = Main.enterTaskId(scanner);

        while (!relevantTaskList.containsKey(targetId)) {
            System.out.println("Задачи с таким идентификатором нет."
                    + " Пожалуйста, введите идентификатор существующей задачи.");
            Main.printTaskList(relevantTaskList);
            targetId = Main.enterTaskId(scanner);
        }

        newTask.setId(targetId);
        integrateNewTask(newTask);

        System.out.println("Задача заменена.");
        System.out.println();
    }

    public void removeTask(Scanner scanner) {
        String taskType = Main.chooseTasksType(scanner);
        Map<Integer, ? extends Task> relevantTaskList = getTasksList(taskType);
        int targetId;

        if (relevantTaskList.isEmpty()) {
            System.out.println("Нет ни одной задачи данного типа.");
            System.out.println();
            return;
        }

        System.out.println("Введите идентификатор удаляемой задачи:");
        Main.printTaskList(relevantTaskList);
        targetId = Main.enterTaskId(scanner);

        while (!relevantTaskList.containsKey(targetId)) {
            System.out.println("Задачи с таким идентификатором нет."
                    + " Пожалуйста, введите идентификатор существующей задачи.");
            Main.printTaskList(relevantTaskList);
            targetId = Main.enterTaskId(scanner);
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
                refreshEpicStatus(parentEpicId);
                subTasksList.remove(targetId);
        }
        System.out.println("Задача удалена.");
        System.out.println();
    }

    public void refreshEpicStatus(int parentEpicId) {
        int subTasksNumber = epicsList.get(parentEpicId).getSubTasksIds().size();
        int unfinishedSubTasksNumber = epicsList.get(parentEpicId).getUnfinishedTasksIds().size();

        if (subTasksNumber == unfinishedSubTasksNumber) {
            epicsList.get(parentEpicId).setStatus("NEW");

        } else if (unfinishedSubTasksNumber > 0) {
            epicsList.get(parentEpicId).setStatus("IN_PROGRESS");

        } else {
            epicsList.get(parentEpicId).setStatus("DONE");
        }
    }
}
