package ru.practicum.task_tracker.manager;

import ru.practicum.task_tracker.Managers;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected static Map<Integer, Task> tasks = new HashMap<>();
    protected static Map<Integer, Epic> epics = new HashMap<>();
    protected static Map<Integer, Subtask> subtasks = new HashMap<>();
    protected static int nextId;
    protected Set<Task> sortedEntity = new TreeSet<>(this::sorted);
    HistoryManager historyManager = Managers.getDefaultHistory();

    protected int sorted(Task o1, Task o2) {
        LocalDateTime startTime1 = o1.getStartTime();
        LocalDateTime startTime2 = o2.getStartTime();

        if (startTime1 == null || startTime2 == null) {
            return -1;
        }
        return startTime1.compareTo(startTime2);
    }

    protected void synchronizeSortedEntity() {
        sortedEntity.clear();
        sortedEntity.addAll(tasks.values());
        sortedEntity.addAll(subtasks.values());

    }

    public Set<Task> getPrioritizedTasks() {
        return sortedEntity;
    }

    public boolean checkPeriodCrossing(LocalDateTime startTime, LocalDateTime endTime) {
        return sortedEntity.stream()
                .anyMatch(e ->
                        !(e.getStartTime().isBefore(startTime) && e.getEndTime().isBefore(startTime)
                                ||
                                e.getStartTime().isAfter(endTime) && e.getEndTime().isAfter(endTime))
                );
    }

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
        if (!tasks.containsKey(taskId) || taskId == null) {
            return null;
        }
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) throws Exception {
        if (checkPeriodCrossing(task.getStartTime(), task.getEndTime())) {
            throw new Exception("Период задачи пересекается с текущими");
        }
        task.setId(getNextId());
        int id = task.getId();

        tasks.put(id, task);
        synchronizeSortedEntity();
        return tasks.get(id);
    }

    @Override
    public Task updateTask(Task task) throws Exception {
        Task oldTask = tasks.get(task.getId());
        if (oldTask.getStartTime() != task.getStartTime() || oldTask.getDuration() != task.getDuration()) {
            if (checkPeriodCrossing(task.getStartTime(), task.getEndTime())) {
                throw new Exception("Период задачи пересекается с текущими");
            }
        }
        Integer taskId = task.getId();
        if (taskId == null || !tasks.containsKey(taskId)) {
            return null;
        }
        tasks.put(taskId, task);
        synchronizeSortedEntity();
        return tasks.get(taskId);
    }

    @Override
    public boolean deleteTask(int taskId) {
        historyManager.remove(taskId);
        boolean result = tasks.remove(taskId) != null;
        synchronizeSortedEntity();
        return result;
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
        boolean result = epics.remove(epicId) != null;
        historyManager.remove(epicId);
        synchronizeSortedEntity();
        return result;
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epic.setStatus(calculateStatusEpic(epic.getSubtasks()));
        } else {
            System.out.println("Эпик не найден");
        }
    }

    private Status calculateStatusEpic(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            return Status.NEW;
        }
        Map<String, Long> statusCount = subtasks.stream()
                .map(Subtask::getStatus)
                .map(Status::toString)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        int sizeSubtasks = subtasks.size();
        Long countNew = statusCount.get(Status.NEW.toString());
        Long countDone = statusCount.get(Status.DONE.toString());
        if (countNew != null && countNew == sizeSubtasks) {
            return Status.NEW;
        }
        if (countDone != null && countDone == sizeSubtasks) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
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
    public Subtask createSubtask(Subtask subtask) throws Exception {
        if (checkPeriodCrossing(subtask.getStartTime(), subtask.getEndTime())) {
            throw new Exception("Период задачи пересекается с текущими createSubtask");
        }
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
        synchronizeSortedEntity();
        return subtasks.get(id);
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws Exception {
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask.getStartTime() != subtask.getStartTime() || oldSubtask.getDuration() != subtask.getDuration()) {
            if (checkPeriodCrossing(subtask.getStartTime(), subtask.getEndTime())) {
                throw new Exception("Период задачи пересекается с текущими updateSubtask");
            }
        }
        Integer subtaskId = subtask.getId();
        Epic epic = epics.get(subtask.getEpicId());
        if (subtaskId == null || !subtasks.containsKey(subtaskId)) {
            return null;
        }
        subtasks.put(subtaskId, subtask);
        updateStatusEpic(epic);
        synchronizeSortedEntity();
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
        synchronizeSortedEntity();
        return removesubtask;
    }

    @Override
    public void deleteAllTasks() {
        subtasks.clear();
        tasks.clear();
        epics.clear();
        historyManager.removeAll();
        nextId = -1;
        sortedEntity.clear();
    }

    private int getNextId() {
        return nextId += 1;
    }

}
