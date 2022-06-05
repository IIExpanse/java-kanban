package managers;

import collections.CustomLinkedList;
import tasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList tasksHistory = new CustomLinkedList();

    @Override
    public void add(Task task) {
        if (task != null) {
            tasksHistory.addTask(task);
        }
    }

    @Override
    public void remove(int id) {
        tasksHistory.removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory.getTasks();
    }
}
