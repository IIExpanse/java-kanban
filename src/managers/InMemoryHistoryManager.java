package managers;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> tasksHistory = new LinkedList<>();
    private final static int HISTORY_MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        if (task != null) {
            tasksHistory.add(task);

            if (tasksHistory.size() > HISTORY_MAX_SIZE) {
                tasksHistory.remove(0);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(tasksHistory);
    }
}
