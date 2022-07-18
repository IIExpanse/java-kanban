package managers.http;

import com.google.gson.Gson;
import managers.filebacked.FileBackedTasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import web.ManagerDataContainer;
import web.KVTaskClient;

import java.io.IOException;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    private final String managerServerKey;
    private final Gson gson = new Gson();


    public HTTPTaskManager(String serverURL) throws IOException, InterruptedException {
        super(serverURL);
        this.client = new KVTaskClient(serverURL);
        this.managerServerKey = "superSecretKey";
        loadFromServer();
    }

    public HTTPTaskManager(String serverURL, String managerServerKey) throws IOException, InterruptedException {
        super(serverURL);
        this.client = new KVTaskClient(serverURL);
        this.managerServerKey = managerServerKey;
        loadFromServer();
    }

    @Override
    protected void save() {
        String serializedData;

        Task[] tasksArr = this.tasksMap.values().toArray(Task[]::new);
        Epic[] epicsArr = this.epicsMap.values().toArray(Epic[]::new);
        SubTask[] subTasksArr = this.subTasksMap.values().toArray(SubTask[]::new);
        Integer[] historyIds = historyManager.getHistory().stream().map(Task::getId).toArray(Integer[]::new);

        serializedData = gson.toJson(new ManagerDataContainer(tasksArr, epicsArr, subTasksArr, historyIds));
        client.put(managerServerKey, serializedData);
    }

    private void loadFromServer() {
        String serializedData = client.load(managerServerKey);
        if (serializedData == null) {
            return;
        }

        ManagerDataContainer container = gson.fromJson(serializedData, ManagerDataContainer.class);
        Task[] tasksArr = container.tasksArr;
        Epic[] epicsArr = container.epicsArr;
        SubTask[] subTasksArr = container.subTasksArr;
        Integer[] historyIds = container.historyIds;

        for (Task task : tasksArr) {
            this.tasksMap.put(task.getId(), task);
            this.tasksFilteredByTime.add(task);
            this.changeTimeIntervals(task, true);
        }

        for (Epic epic : epicsArr) {
            this.epicsMap.put(epic.getId(), epic);
        }

        for (SubTask subTask : subTasksArr) {
            this.subTasksMap.put(subTask.getId(), subTask);
            this.tasksFilteredByTime.add(subTask);
            this.changeTimeIntervals(subTask, true);
        }

        for (int id : historyIds) {
            Task task;

            if (tasksMap.containsKey(id)) {
                task = tasksMap.get(id);
            } else if (epicsMap.containsKey(id)) {
                task = epicsMap.get(id);
            } else {
                task = subTasksMap.get(id);
            }
            historyManager.add(task);
        }
    }
}
