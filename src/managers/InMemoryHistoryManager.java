package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> tasksHistory = new ArrayList<>();
    private final int HISTORY_MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        tasksHistory.add(task);

        if (tasksHistory.size() > HISTORY_MAX_SIZE) {
            tasksHistory.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(tasksHistory);
    }
}
