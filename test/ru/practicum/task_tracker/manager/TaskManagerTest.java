package ru.practicum.task_tracker.manager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.Managers;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {
    LocalDateTime time1 = LocalDateTime.of(2020, 1, 1, 13, 0);
    TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = Managers.getDefaultHistory();

    Task task1 = new Task(0, "таск1.Имя", "таск1.Описание", Status.NEW, 23, time1);
    Task task2 = new Task(1, "таск2.Имя", "таск2.Описание",
            Status.NEW, 23, time1.plusDays(1));
    Task task3 = new Task(2, "таск3.Имя", "таск3.Описание",
            Status.NEW, 23, time1.plusDays(2));
    Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
    Epic epic2 = new Epic("Поход в магазин", "за продуктами");


    @AfterEach
    void clearManager() {
        taskManager.deleteAllTasks();
    }

    @Test
    void checkStatusEpic() throws Exception {
//        Все подзадачи со статусом NEW.
        Epic savedEpic = taskManager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 24, time1.plusDays(4));
        taskManager.createSubtask(subtask1ForEpic1);
        Subtask subtask2ForEpic1 = new Subtask(savedEpic.getId(), "Не забыть: ",
                "палатку, пенки", Status.NEW, 25, time1.plusDays(5));
        taskManager.createSubtask(subtask2ForEpic1);
        assertEquals(epic1.getStatus(), Status.NEW);

//        Все подзадачи со статусом DONE
        subtask1ForEpic1.setStatus(Status.DONE);
        subtask2ForEpic1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1ForEpic1);
        taskManager.updateSubtask(subtask2ForEpic1);
        assertEquals(epic1.getStatus(), Status.DONE);
//        Подзадачи со статусами NEW и DONE.
        subtask1ForEpic1.setStatus(Status.NEW);
        subtask2ForEpic1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1ForEpic1);
        taskManager.updateSubtask(subtask2ForEpic1);
        assertEquals(epic1.getStatus(), Status.IN_PROGRESS);

//        Подзадачи со статусом IN_PROGRESS.
        subtask1ForEpic1.setStatus(Status.IN_PROGRESS);
        subtask2ForEpic1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1ForEpic1);
        taskManager.updateSubtask(subtask2ForEpic1);
        assertEquals(epic1.getStatus(), Status.IN_PROGRESS);

//         Для подзадач нужно дополнительно проверить наличие эпика
        assertEquals(epic1.getId(), subtask1ForEpic1.getEpicId());
        assertEquals(epic1.getId(), subtask2ForEpic1.getEpicId());
    }

    @Test
    void getHistory() throws Exception {
        taskManager.createTask(task1);
        taskManager.getTasks();
        List<Task> listHistory = taskManager.getHistory();
        assertEquals(listHistory.size(), 1);
        assertEquals(listHistory.getFirst(), task1);
    }

    @Test
    void getTasks() throws Exception {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> listTasks = taskManager.getTasks();
        assertEquals(listTasks.size(), 2);
        assertEquals(listTasks.getFirst(), task1);
        assertEquals(listTasks.getLast(), task2);
    }

    @Test
    void getByTaskId() throws Exception {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertEquals(taskManager.getTasks().size(), 2);
        assertEquals(taskManager.getByTaskId(task1.getId()), task1);
        assertEquals(taskManager.getByTaskId(task2.getId()), task2);
        assertNull(taskManager.getByTaskId(task3.getId()));
    }

    @Test
    void createTask() throws Exception {
        Task createdTask1 = taskManager.createTask(task1);
        Task createdTask2 = taskManager.createTask(task2);
        assertEquals(task1, createdTask1);
        assertEquals(task2, createdTask2);
    }


    @Test
    void updateTask() throws Exception {
        Task createdTask1 = taskManager.createTask(task1);
        taskManager.createTask(task2);
        createdTask1.setStatus(Status.DONE);
        Task updateTask = taskManager.updateTask(createdTask1);
        assertEquals(createdTask1, updateTask);
        assertNull(taskManager.getByTaskId(task3.getId()));
    }

    @Test
    void deleteTask() throws Exception {
        Task createdTask1 = taskManager.createTask(task1);
        taskManager.createTask(task2);
        assertTrue(taskManager.deleteTask(createdTask1.getId()));
        assertFalse(taskManager.deleteTask(task3.getId()));
    }

    @Test
    void getEpics() {
        Epic savedEpic1 = taskManager.createEpic(epic1);
        Epic savedEpic2 = taskManager.createEpic(epic2);
        List<Epic> listEpics = taskManager.getEpics();
        assertEquals(listEpics.size(), 2);
        assertEquals(listEpics.getFirst(), savedEpic1);
        assertEquals(listEpics.getLast(), savedEpic2);
    }

    @Test
    void getByEpicId() {
        Epic createdEpic1 = taskManager.createEpic(epic1);
        assertEquals(taskManager.getByEpicId(createdEpic1.getId()), createdEpic1);
        assertNull(taskManager.getByEpicId(epic2.getId()));
    }


    @Test
    void createEpic() {
        Epic createdEpic1 = taskManager.createEpic(epic1);
        assertEquals(epic1, createdEpic1);
    }

    @Test
    void updateEpic() {
        Epic createdEpic1 = taskManager.createEpic(epic1);

        createdEpic1.setName("123");
        Epic updateEpic1 = taskManager.updateEpic(createdEpic1);

        assertEquals(createdEpic1, updateEpic1);
    }

    @Test
    void deleteEpic() {
        Epic createdEpic1 = taskManager.createEpic(epic1);
        assertTrue(taskManager.deleteEpic(createdEpic1.getId()));
    }


    @Test
    void getSubtasks() throws Exception {
        Epic createdEpic1 = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(createdEpic1.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));
        Subtask subtask2ForEpic1 = new Subtask(createdEpic1.getId(), "Не забыть: ",
                "палатку, пенки", Status.NEW, 13, time1.plusDays(3));

        Subtask createdSubtask1 = taskManager.createSubtask(subtask1ForEpic1);
        Subtask createdSubtask2 = taskManager.createSubtask(subtask2ForEpic1);

        ArrayList<Subtask> subtasks = new ArrayList<>(taskManager.getSubtasks());

        assertEquals(subtasks.get(0), createdSubtask1);
        assertEquals(subtasks.get(1), createdSubtask2);
    }

    @Test
    void getBySubtaskId() throws Exception {
        Epic createdEpic1 = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(createdEpic1.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));
        Subtask subtask2ForEpic1 = new Subtask(createdEpic1.getId(), "Не забыть: ",
                "палатку, пенки", Status.NEW, 13, time1.plusDays(3));

        Subtask createdSubtask1 = taskManager.createSubtask(subtask1ForEpic1);
        Subtask createdSubtask2 = taskManager.createSubtask(subtask2ForEpic1);

        int subtask1Id = createdSubtask1.getId();
        int subtask2Id = createdSubtask2.getId();

        assertEquals(taskManager.getBySubtaskId(subtask1Id), createdSubtask1);
        assertEquals(taskManager.getBySubtaskId(subtask2Id), createdSubtask2);
    }

    @Test
    void createSubtask() throws Exception {
        Epic createdEpic1 = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(createdEpic1.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));

        Subtask createdSubtask1 = taskManager.createSubtask(subtask1ForEpic1);

        assertEquals(createdSubtask1, subtask1ForEpic1);
    }

    @Test
    void updateSubtask() throws Exception {
        Epic createdEpic1 = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(createdEpic1.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));

        Subtask createdSubtask1 = taskManager.createSubtask(subtask1ForEpic1);
        createdSubtask1.setStatus(Status.IN_PROGRESS);
        Subtask updateSubtask = taskManager.updateSubtask(createdSubtask1);

        assertEquals(createdSubtask1, updateSubtask);
    }

    @Test
    void deleteSubtask() throws Exception {
        Epic createdEpic1 = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(createdEpic1.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));

        Subtask createdSubtask1 = taskManager.createSubtask(subtask1ForEpic1);
        int idSubtask1 = createdSubtask1.getId();

        assertTrue(taskManager.deleteSubtask(idSubtask1));
    }

    @Test
    void deleteAllTasks() throws Exception {
        Epic createdEpic1 = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(createdEpic1.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));
        Subtask subtask2ForEpic1 = new Subtask(createdEpic1.getId(), "Не забыть: ",
                "палатку, пенки", Status.NEW, 13, time1.plusDays(3));

        taskManager.createTask(task1);
        taskManager.createSubtask(subtask1ForEpic1);
        taskManager.createSubtask(subtask2ForEpic1);

        taskManager.getTasks();
        taskManager.getEpics();
        taskManager.getSubtasks();

        taskManager.deleteAllTasks();

        assertEquals(taskManager.getTasks().size(), 0);
        assertEquals(taskManager.getEpics().size(), 0);
        assertEquals(taskManager.getSubtasks().size(), 0);
        assertEquals(taskManager.getHistory().size(), 0);
    }


}