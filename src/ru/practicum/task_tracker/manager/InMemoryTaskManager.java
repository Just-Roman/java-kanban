package ru.practicum.task_tracker.manager;

import ru.practicum.task_tracker.Managers;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = Managers.getDefaultHistory();

    protected static Map<Integer, Task> tasks = new HashMap<>();
    protected static Map<Integer, Epic> epics = new HashMap<>();
    protected static Map<Integer, Subtask> subtasks = new HashMap<>();
    protected static int nextId;

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getTasks() {
        ArrayList<Task> taskValues = new ArrayList<>(tasks.values());
        for (Task task : taskValues) {
            historyManager.add(task);
        }
        return taskValues;
    }

    public Task getByTaskId(Integer taskId) {
        if (taskId == null || !tasks.containsKey(taskId)) {
            return null;
        }
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        task.setId(getNextId());
        int id = task.getId();
        tasks.put(id, task);
        return tasks.get(id);
    }

    @Override
    public Task updateTask(Task task) {
        Integer taskId = task.getId();
        if (taskId == null || !tasks.containsKey(taskId)) {
            return null;
        }
        tasks.put(taskId, task);
        return tasks.get(taskId);
    }

    @Override
    public boolean deleteTask(int taskId) {
        historyManager.remove(taskId);
        return tasks.remove(taskId) != null;
    }

    @Override
    public List<Epic> getEpics() {
        ArrayList<Epic> epicValues = new ArrayList<>(epics.values());
        for (Epic epic : epicValues) {
            historyManager.add(epic);
        }
        return epicValues;
    }

    public Epic getByEpicId(Integer epicId) {
        if (epicId == null || !epics.containsKey(epicId)) {
            return null;
        }
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        int id = epic.getId();
        epics.put(id, epic);
        updateStatusEpic(epic);
        return epics.get(id);
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (epicId == null || !epics.containsKey(epicId)) {
            return null;
        }
        epics.put(epicId, epic);
        updateStatusEpic(epic);
        return epics.get(epicId);
    }

    @Override
    public boolean deleteEpic(int epicId) {
        Epic epic = epics.get(epicId);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        historyManager.remove(epicId);
        return epics.remove(epicId) != null;
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            if (epic.getSubtasks().isEmpty()) {
                epic.setStatus(Status.NEW);
            } else {
                int statusDone = 0;
                int statusNew = 0;
                for (Subtask subtask : epic.getSubtasks()) {
                    if (subtask.getStatus() == Status.DONE) {
                        statusDone++;
                    }
                    if (subtask.getStatus() == Status.NEW) {
                        statusNew++;
                    }
                    if (subtask.getStatus() == Status.IN_PROGRESS) {
                        epic.setStatus(Status.IN_PROGRESS);
                        return;
                    }
                }
                if (statusDone == epic.getSubtasks().size()) {
                    epic.setStatus(Status.DONE);
                } else if (statusNew == epic.getSubtasks().size()) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.IN_PROGRESS);
                }
            }
        } else {
            System.out.println("Эпик не найден");
        }
    }

    @Override
    public List<Subtask> getSubtasks() {
        ArrayList<Subtask> subtasksValues = new ArrayList<>(subtasks.values());
        for (Subtask subtask : subtasksValues) {
            historyManager.add(subtask);
        }
        return subtasksValues;
    }

    public Subtask getBySubtaskId(Integer subtaskId) {
        if (subtaskId == null || !subtasks.containsKey(subtaskId)) {
            return null;
        }
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            return null;
        }
        subtask.setId(getNextId());
        Epic epic = epics.get(subtask.getEpicId());
        epic.setSubtasks(subtask);
        int id = subtask.getId();
        subtasks.put(id, subtask);
        updateStatusEpic(epic);
        return subtasks.get(id);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        Epic epic = epics.get(subtask.getEpicId());
        if (subtaskId == null || !subtasks.containsKey(subtaskId)) {
            return null;
        }
        subtasks.put(subtaskId, subtask);
        updateStatusEpic(epic);
        return subtasks.get(subtaskId);
    }

    @Override
    public boolean deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        boolean removesubtask = subtasks.remove(subtask.getId()) != null;
        historyManager.remove(subtaskId);

        Epic epic = epics.get(subtask.getEpicId());
        epic.removesubtaskById(subtask);

        updateStatusEpic(epic);
        return removesubtask;
    }

    @Override
    public void deleteAllTasks() {
        subtasks.clear();
        tasks.clear();
        epics.clear();
        historyManager.removeAll();
        nextId = -1;
    }

    private int getNextId() {
        return nextId++;
    }

}
