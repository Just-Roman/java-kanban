package ru.practicum.task_tracker;

import ru.practicum.task_tracker.manager.*;

public class Managers {

    public static TaskManager getDefault() {
        return new FileBackedTaskManager();
       // return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}