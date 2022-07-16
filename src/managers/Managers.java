package managers;

import managers.filebacked.FileBackedTasksManager;
import managers.http.HTTPTaskManager;
import managers.inmemory.InMemoryHistoryManager;

import java.io.IOException;

public class Managers {

    private Managers() {
    }

    public static TasksManager getDefault(String serverUrl) throws IOException, InterruptedException {
        return new HTTPTaskManager(serverUrl);
    }

    public static TasksManager getFileBackedTasks(String filePath) {
        return new FileBackedTasksManager(filePath);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

