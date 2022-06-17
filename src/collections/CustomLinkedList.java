package collections;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList {
    private final Map<Integer, Node<Task>> map = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    public void addTask(Task task) {
        if (task != null) {
            int taskId = task.getId();

            if (map.containsKey(taskId)) {
                removeNode(map.get(taskId));
            }

            Node<Task> newNode = new Node<>(null, task, null);

            linkLast(newNode);
            map.put(taskId, newNode);
        }
    }

    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node<Task> nextNode = head;

        while (nextNode != null) {
            list.add(nextNode.data);
            nextNode = nextNode.next;
        }
        return List.copyOf(list);
    }

    public void removeNode(int taskId) {
        this.removeNode(map.get(taskId));
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }

            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }

            map.remove(node.data.getId());
            node.prev = null;
            node.data = null;
            node.next = null;
        }
    }

    private void linkLast(Node<Task> newNode) {
        if (tail != null) {
            newNode.prev = tail;
            tail.next = newNode;
        } else {
            head = newNode;
        }
        tail = newNode;
    }
}
