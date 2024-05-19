package service;

import model.Task;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task data;
        Node next;
        Node prev;

        public Node(Task data, Node prev, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
        Node prev = node.prev;
        Node next = node.next;
        if (prev != null) {
            prev.next = next;
        } else {
            first = next;
        }
        if (next != null) {
            next.prev = prev;
        } else {
            last = prev;
        }
        history.remove(node.data.getId());
    }


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        Node node = history.get(task.getId());
        removeNode(node);
        linkLast(task);
        System.out.println("В историю задач добавлена задача под номером:" + task.getId());
    }

    private void linkLast(Task task) {
        final Node nodeLast = last;
        final Node newNode = new Node(task, nodeLast, null);
        last = newNode;
        if (nodeLast == null) {
            first = newNode;
        } else {
            nodeLast.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }


    @Override
    public List<Task> getHistory() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node current = first;
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }
}
