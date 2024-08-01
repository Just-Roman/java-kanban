package ru.practicum.task_tracker;

import ru.practicum.task_tracker.manager.FileBackedTaskManager;
import ru.practicum.task_tracker.manager.HistoryManager;
import ru.practicum.task_tracker.manager.InMemoryHistoryManager;
import ru.practicum.task_tracker.manager.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager();
       // return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}