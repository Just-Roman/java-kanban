package ru.practicum.task_tracker;

import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.manager.TaskManager;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    void getDefault() {
        // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        Task savedTask = taskManager.createTask(task1);
        assertEquals(task1, savedTask);
        taskManager.deleteAllTasks();
    }

    @Test
    void getDefaultHistory() {
        // убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("таск.Имя", "таск.Описание", Status.NEW);
        Task createdTask = taskManager.createTask(task1);
        taskManager.getTasks();
        assertEquals(createdTask, taskManager.getHistory().getFirst());
    }
}