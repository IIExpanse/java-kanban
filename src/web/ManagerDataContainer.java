package web;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public class ManagerDataContainer {

    public final Task[] tasksArr;
    public final Epic[] epicsArr;
    public final SubTask[] subTasksArr;
    public final Integer[] historyIds;

    public ManagerDataContainer(Task[] tasksArr, Epic[] epicsArr, SubTask[] subTasksArr, Integer[] historyIds) {
        this.tasksArr = tasksArr;
        this.epicsArr = epicsArr;
        this.subTasksArr = subTasksArr;
        this.historyIds = historyIds;
    }
}
