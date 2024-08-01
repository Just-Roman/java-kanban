package ru.practicum.task_tracker.manager;

import ru.practicum.task_tracker.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node first;
    private Node last;
    private Map<Integer, Node> memory = new HashMap<>();

    @Override
    public void add(Task task) {
        if (memory.containsKey(task.getId())) {
            if (memory.size() == 1) {
                return;
            } else if (memory.get(task.getId()) == last) {
                last = last.prev;
            } else if (memory.get(task.getId()) == first) {
                first = first.next;
            }
            remove(task.getId());
        }
        linkLast(task);
    }

    private void linkLast(Task task) {
        if (first == null) {
            Node node = new Node(null, null, task);
            memory.put(task.getId(), node);
            first = node;
            last = node;
        } else {
            Node node = new Node(last, null, task);
            memory.put(task.getId(), node);
            last = node;
            node.prev.next = node;
        }
    }

    @Override
    public boolean remove(int id) {
        Node node = memory.remove(id);

        if (node == null) {
            return false;
        }
        removeNode(node);
        return true;
    }

    @Override
    public void removeAll() {
        memory.clear();
        first = null;
        last = null;
    }

    private void removeNode(Node node) {
        if (node.prev == null && node.next == null) {
            return;
        } else if (node.prev == null) {
            node.next.prev = node.prev;
        } else if (node.next == null) {
            node.prev.next = node.next;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> memoryTasks = new ArrayList<>();

        if (memory.isEmpty()) {
            return memoryTasks;
        } else {
            Node node = first;
            for (int i = 0; i < memory.size(); i++) {
                memoryTasks.add(node.value);
                node = node.next;
            }
        }
        return memoryTasks;
    }

    private static class Node {

        Node prev;
        Node next;
        Task value;

        public Node(Node prev, Node next, Task value) {
            this.prev = prev;
            this.next = next;
            this.value = value;
        }
    }
}

