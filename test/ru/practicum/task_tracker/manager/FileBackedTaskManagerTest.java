package ru.practicum.task_tracker.manager;

import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.Managers;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    TaskManager taskManager = Managers.getDefault();

    @Test
    void loadEmptyFile() {
        // Загрузка пустого файла
        taskManager.deleteAllTasks();
        FileBackedTaskManager.loadFromFile(taskManager);
        List<Task> get1 = taskManager.getTasks();
        List<Epic> get2 = taskManager.getEpics();
        List<Subtask> get3 = taskManager.getSubtasks();

        assertEquals(get1.size(), 0);
        assertEquals(get1.size(), get2.size());
        assertEquals(get1.size(), get3.size());
    }

    @Test
    void saveFromFileTask() {
        // Сохранение в файл
        taskManager.deleteAllTasks();
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        taskManager.createTask(task1);
        FileBackedTaskManager.loadFromFile(taskManager);
        List<Task> createTask1 = taskManager.getTasks();
        assertEquals(createTask1.size(), 1);
    }

    @Test
    void saveFromFile() {
        // сохранение нескольких задач
        taskManager.deleteAllTasks();
        List<Task> tasksGet1 = taskManager.getTasks();
        List<Epic> epicsGet1 = taskManager.getEpics();
        List<Subtask> subtasksGet1 = taskManager.getSubtasks();

        assertEquals(tasksGet1.size(), 0);
        assertEquals(tasksGet1.size(), epicsGet1.size());
        assertEquals(tasksGet1.size(), subtasksGet1.size());

        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task("таск2.Имя", "таск2.Описание", Status.NEW);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        taskManager.createSubtask(subtask1ForEpic1);
        Subtask subtask2ForEpic1 = new Subtask(savedEpic.getId(), "Не забыть: ", "палатку", Status.NEW);
        taskManager.createSubtask(subtask2ForEpic1);

        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        FileBackedTaskManager.loadFromFile(inMemoryTaskManager);

        List<Task> tasksGet2 = taskManager.getTasks();
        List<Epic> epicsGet2 = taskManager.getEpics();
        List<Subtask> subtasksGet2 = taskManager.getSubtasks();

        assertEquals(tasksGet2.size(), 2);
        assertEquals(epicsGet2.size(), 1);
        assertEquals(subtasksGet2.size(), 2);
    }

}