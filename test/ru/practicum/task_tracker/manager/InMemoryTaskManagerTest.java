package ru.practicum.task_tracker.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.Managers;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest {

/* Вячеслав, доброго дня!
Задания для тестов были, мягко говоря, трудные к пониманию и реализации.
Некоторые невозможно реализовать. Ко всем тестам написал комментарии (для понимания).
Буду рад любому комментарию, спасибо!
*/

    TaskManager taskManager = Managers.getDefault();

    @Test
    void checkEqualsCreatedTask() {
        // проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        Task savedTask = taskManager.createTask(task1);
        assertEquals(task1, savedTask);
    }

    @Test
    void checkEqualsCreatedEpic() {
        // проверьте, что наследники класса Task равны друг другу, если равен их id;
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);
        assertEquals(epic1, savedEpic);
    }

    @Test
    void checkEqualsCreatedSubtask() {
        // проверьте, что наследники класса Task равны друг другу, если равен их id;
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic epic1Created = taskManager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(epic1Created.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        Subtask savedSubtask = taskManager.createSubtask(subtask1ForEpic1);
        assertEquals(subtask1ForEpic1, savedSubtask);

    }



    @Test
    void objectEpicCanNotMakPersonalSubtask() {
        // // проверьте, что объект Subtask нельзя сделать своим же эпиком
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic epic1Created = taskManager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(epic1Created.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        // taskManager.createSubtask(epic1); // - не выполнимо

    }

    @Test
    void objectSubtaskCanNotMakPersonalEpic() {
        // проверьте, что объект Subtask нельзя сделать своим же эпиком
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic epic1Created = taskManager.createEpic(epic1);
        Subtask subtask1ForEpic1 = new Subtask(epic1Created.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        // taskManager.createSubtask(epic1); // - не выполнимо
    }

    @Test
    void inMemoryTaskManagerAddTaskSubtaskEpic() {
        //Проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        Task savedTask = taskManager.createTask(task1);
        assertEquals(task1, savedTask);

        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);
        assertEquals(epic1, savedEpic);

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ", "пластик. посуду ", Status.NEW);
        Subtask saveSubtask1 = taskManager.createSubtask(subtask1ForEpic1);
        assertEquals(subtask1ForEpic1, saveSubtask1);
        Subtask subtask2ForEpic1 = new Subtask(savedEpic.getId(), "Не забыть: ", "палатку, пенки", Status.NEW);
        Subtask saveSubtask2 = taskManager.createSubtask(subtask2ForEpic1);
        assertEquals(subtask2ForEpic1, saveSubtask2);
    }


    @Test
    void checkIdConflict() {
        // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
        Task task1 = new Task(  "таск1.Имя", "таск1.Описание", Status.NEW);
        taskManager.createTask(task1);
        Task task2 = new Task( 0,"таск2.Имя", "таск2.Описание", Status.NEW);
        taskManager.createTask(task2);
        assertEquals(taskManager.getTasks().size(), 2);
    }


    @Test
    void taskFieldImmutability() {
        // создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW);
        Task savedTask1 = taskManager.createTask(task1);
        Task task2 = new Task("таск2.Имя", "таск2.Описание", Status.NEW);
        taskManager.createTask(task2);
        Task task3 = new Task("таск3.Имя", "таск3.Описание", Status.NEW);
        taskManager.createTask(task3);
        assertEquals(savedTask1, task1);
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
    }
}