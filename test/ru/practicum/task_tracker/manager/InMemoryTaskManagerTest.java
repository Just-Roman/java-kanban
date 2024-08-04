package ru.practicum.task_tracker.manager;

import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.Managers;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InMemoryTaskManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void checkEqualsCreatedTask() {
        // проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        taskManager.createTask(task1);
        Task getTask = taskManager.getByTaskId(task1.getId());
        assertEquals(task1, getTask);
        taskManager.deleteAllTasks();
    }

    @Test
    void checkEqualsCreatedEpic() {
        // проверьте, что наследники класса Task равны друг другу, если равен их id;
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);
        assertEquals(epic1, savedEpic);
        taskManager.deleteAllTasks();
    }

    @Test
    void checkEqualsCreatedSubtask() {
        // проверьте, что наследники класса Task равны друг другу, если равен их id;
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic epic1Created = taskManager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(epic1Created.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        Subtask savedSubtask = taskManager.createSubtask(subtask1ForEpic1);
        assertEquals(subtask1ForEpic1, savedSubtask);
        taskManager.deleteAllTasks();
    }

    @Test
    void objectEpicCanNotMakePersonalSubtask() {
        // // проверьте, что объект Subtask нельзя сделать своим же эпиком
        Subtask subtask1 = new Subtask(9, "Купить: ", "пластик. посуду ", Status.NEW);
        assertNull(taskManager.createSubtask(subtask1));
        taskManager.deleteAllTasks();
    }

    @Test
    void inMemoryTaskManagerAddTaskSubtaskEpic() {
        //Проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        Task savedTask = taskManager.createTask(task1);
        assertEquals(task1.getId(), savedTask.getId());

        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);
        assertEquals(epic1.getId(), savedEpic.getId());

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        Subtask saveSubtask1 = taskManager.createSubtask(subtask1ForEpic1);
        assertEquals(subtask1ForEpic1.getId(), saveSubtask1.getId());
        Subtask subtask2ForEpic1 = new Subtask(savedEpic.getId(), "Не забыть: ", "палатку, пенки", Status.NEW);
        Subtask saveSubtask2 = taskManager.createSubtask(subtask2ForEpic1);
        assertEquals(subtask2ForEpic1.getId(), saveSubtask2.getId());
        taskManager.deleteAllTasks();
    }

    @Test
    void checkIdConflict() {
        // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task(0, "таск2.Имя", "таск2.Описание", Status.NEW);
        taskManager.createTask(task2);
        assertEquals(taskManager.getTasks().size(), 2);
        taskManager.deleteAllTasks();
    }

    @Test
    void taskFieldImmutability() {
        // создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        Task savedTask1 = taskManager.createTask(task1);
        Task task2 = new Task("таск2.Имя", "таск2.Описание", Status.NEW);
        Task savedTask2 = taskManager.createTask(task2);
        Task task3 = new Task("таск3.Имя", "таск3.Описание", Status.NEW);
        Task savedTask3 = taskManager.createTask(task3);
        assertEquals(savedTask1.getId(), task1.getId());
        assertEquals(savedTask2.getId(), task2.getId());
        assertEquals(savedTask3.getId(), task3.getId());
        taskManager.deleteAllTasks();
    }

    @Test
    void checkSaveLastVersionHistoryManager() {
        // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        Task savedTask = taskManager.createTask(task1);
        Integer taskId = savedTask.getId();
        taskManager.getTasks();

        savedTask.setStatus(Status.DONE);
        taskManager.updateTask(task1);
        taskManager.getTasks();
        List<Task> history = new ArrayList<>(taskManager.getHistory());
        for (Task taskGetTask1 : history) {
            if (taskGetTask1.getId() == taskId && taskGetTask1.getStatus() == Status.NEW) {
                assertEquals(taskGetTask1, task1);
            }
        }
        taskManager.deleteAllTasks();
    }

    @Test
    void checkDeleteIdSubtask() {
        // Удаляемые подзадачи не должны хранить внутри себя старые id.
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        Subtask saveSubtask1 = taskManager.createSubtask(subtask1ForEpic1);

        taskManager.deleteSubtask(saveSubtask1.getId());
        Subtask getDeleteId = taskManager.getBySubtaskId(saveSubtask1.getId());
        Integer deleteEpicIdInSubtask = saveSubtask1.getEpicId();

        assertNull(getDeleteId);
        assertEquals(deleteEpicIdInSubtask, 0);
        taskManager.deleteAllTasks();
    }

    @Test
    void checkDeleteSubtaskForEpic() {
        //  Внутри эпиков не должно оставаться неактуальных id подзадач.
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        Subtask saveSubtask1 = taskManager.createSubtask(subtask1ForEpic1);

        taskManager.deleteSubtask(saveSubtask1.getId());

        Subtask getDeleteId = taskManager.getBySubtaskId(saveSubtask1.getId());
        List<Subtask> subtaskInEpic = savedEpic.getSubtasks();

        Integer subtask = 0;
        if (subtaskInEpic.isEmpty()) {
            subtask = null;
        }
        assertNull(getDeleteId);
        assertEquals(getDeleteId, subtask);
        taskManager.deleteAllTasks();
    }

    @Test
    void checkChangeSetter() {
        // С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        Task savedTask1 = taskManager.createTask(task1);
        task1.setId(9);
        assertEquals(savedTask1.getId(), 9);
    }

}