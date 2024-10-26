package ru.practicum.task_tracker.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private static final String HOME = System.getProperty("user.dir");
    private static final String fileName = "tasksFile.csv";
    private static final Path TASKS_FILE_PATH = Paths.get(HOME, fileName);
    LocalDateTime time1 = LocalDateTime.of(2020, 1, 1, 13, 0);
    TaskManager taskManager = new FileBackedTaskManager(getFile());

    @AfterEach
    void clearManager() {
        taskManager.deleteAllTasks();
    }

    private File getFile() {
        try {
            if (TASKS_FILE_PATH.getFileName() == null) {
                return File.createTempFile(HOME, fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return TASKS_FILE_PATH.toFile();
    }

    @Test
    public void testException() {
//        Корректный перехват исключений при работе с файлами
        Path wrongPath = Paths.get(HOME, "sddfd/_.");
        Assertions.assertThrows(RuntimeException.class, () -> FileBackedTaskManager.loadFromFile(wrongPath));
        Assertions.assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(TASKS_FILE_PATH));
    }

    @Test
    void loadEmptyFile() {
        // Загрузка пустого файла
        FileBackedTaskManager.loadFromFile(TASKS_FILE_PATH);
        List<Task> get1 = taskManager.getTasks();
        List<Epic> get2 = taskManager.getEpics();
        List<Subtask> get3 = taskManager.getSubtasks();

        assertEquals(get1.size(), 0);
        assertEquals(get1.size(), get2.size());
        assertEquals(get1.size(), get3.size());
    }

    @Test
    void saveFromFileTask() throws Exception {
        // Сохранение в файл
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW, 10, time1);
        Task createdTask1 = taskManager.createTask(task1);
        FileBackedTaskManager.loadFromFile(TASKS_FILE_PATH);
        Task task2 = taskManager.getByTaskId(task1.getId());

        assertEquals(createdTask1.getId(), task2.getId());
        assertEquals(createdTask1.getName(), task2.getName());
        assertEquals(createdTask1.getDescription(), task2.getDescription());
        assertEquals(createdTask1.getStatus(), task2.getStatus());
    }

    @Test
    void saveFromFile() throws Exception {
        // сохранение нескольких задач

        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW, 11, time1.plusDays(1));
        Task createdTask1 = taskManager.createTask(task1);
        Task task2 = new Task("таск2.Имя", "таск2.Описание", Status.NEW, 12, time1.plusDays(2));
        Task createdTask2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 13, time1.plusDays(3));
        Subtask createdSubtask1 = taskManager.createSubtask(subtask1ForEpic1);
        Subtask subtask2ForEpic1 = new Subtask(savedEpic.getId(), "Не забыть: ",
                "палатку", Status.NEW, 14, time1.plusDays(4));
        Subtask createdSubtask2 = taskManager.createSubtask(subtask2ForEpic1);

        FileBackedTaskManager.loadFromFile(TASKS_FILE_PATH);

        List<Task> tasksGet2 = taskManager.getTasks();
        List<Epic> epicsGet2 = taskManager.getEpics();
        List<Subtask> subtasksGet2 = taskManager.getSubtasks();

        assertEquals(tasksGet2.size(), 2);
        assertEquals(epicsGet2.size(), 1);
        assertEquals(subtasksGet2.size(), 2);
    }

}