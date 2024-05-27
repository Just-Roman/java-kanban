package ru.practicum.task_tracker.manager;

import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();

    private int nextId;

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task createTask(Task task) {
        task.setId(getNextId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Task task) {
        Integer taskId = task.getId();
        if (taskId == null || !tasks.containsKey(taskId)) {
            return null;
        }
        tasks.put(taskId, task);
        return task;
    }

    public boolean deliteTask(int taskId) {
       return tasks.remove(taskId) != null;
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic createEpic(Epic epic) {
        epic.setId(getNextId());
        epics.put(epic.getId(), epic);
        updateStatusEpic(epic);
        return epic;
    }

    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (epicId == null || !epics.containsKey(epicId)) {
            return null;
        }
        epics.put(epicId, epic);
        updateStatusEpic(epic);
        return epic;
    }

    public boolean deleteEpic(int epicId) {
        Epic epic = epics.get(epicId);
        for (Subtask subtask : epic.getSubtasks()) {
            subtasks.remove(subtask.getId());
        }
        return epics.remove(epicId) != null;
    }

    private void updateStatusEpic(Epic epic) {
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

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(getNextId());
        Epic epic = epics.get(subtask.getEpicId());
        epic.setSubtasks(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateStatusEpic(epic);
        return subtasks.get(subtask.getId());
    }

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

    public boolean deleteSubtask(Integer id) {
        Subtask subtask = subtasks.get(id);
        boolean removesubtask = subtasks.remove(subtask.getId()) != null;

        Epic epic = epics.get(subtask.getEpicId());
        epic.removesubtaskById(subtask);

         updateStatusEpic(epic);
        return removesubtask;
    }

    public void deleteAllTasks() {
        subtasks.clear();
        tasks.clear();
        epics.clear();
    }


    private int getNextId() {
        return  nextId++;
    }
}
