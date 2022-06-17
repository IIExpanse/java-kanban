package managers;

import managers.filebacked.FileBackedTasksManager;
import managers.inmemory.InMemoryHistoryManager;
import managers.inmemory.InMemoryTasksManager;

public class Managers {

    private Managers() {
    }

    public static TasksManager getDefault() {
        return new InMemoryTasksManager();
    }

    public static TasksManager getFileBackedTasks(String filePath) {
        return new FileBackedTasksManager(filePath);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}

