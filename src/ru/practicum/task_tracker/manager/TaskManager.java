package ru.practicum.task_tracker.manager;

import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.util.List;

public interface TaskManager {

    public List getHistory();

    List<Task> getTasks();

    Task createTask(Task task);

    Task updateTask(Task task);

    boolean deliteTask(int taskId);

    List<Epic> getEpics();

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    boolean deleteEpic(int epicId);

    void updateStatusEpic(Epic epic);

    List<Subtask> getSubtasks();

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    boolean deleteSubtask(Integer id);

    void deleteAllTasks();
}
