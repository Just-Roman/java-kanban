package ru.practicum.task_tracker.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.Managers;
import ru.practicum.task_tracker.http.Exception.NotFoundException;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    TaskManager taskManager = Managers.getDefault();
    LocalDateTime time1 = LocalDateTime.of(2020, 1, 1, 13, 0);

    @BeforeEach
    void clearManager() {
        taskManager.deleteAllTasks();
    }

    @Test
    void checkTreeSet() {
//        Проверка сортировки списка
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW, 23, time1);
        taskManager.createTask(task1);

        Task task2 = new Task("таск2.Имя", "таск2.Описание", Status.NEW, 24, time1.plusDays(1));
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ",
                "пластик. посуду ", Status.DONE, 24, time1.plusDays(2));
        taskManager.createSubtask(subtask1ForEpic1);

        Subtask subtask2ForEpic1 = new Subtask(savedEpic.getId(), "Не забыть: ",
                "палатку, пенки", Status.DONE, 25, time1.plusDays(3));
        taskManager.createSubtask(subtask2ForEpic1);

        List<Task> sortedEntity = new ArrayList<>(taskManager.getPrioritizedTasks());

        assertEquals(sortedEntity.get(0), task1);
        assertEquals(sortedEntity.get(1), task2);
        assertEquals(sortedEntity.get(2), subtask1ForEpic1);
        assertEquals(sortedEntity.get(3), subtask2ForEpic1);
        assertEquals(sortedEntity.size(), 4);
    }


    @Test
    void checkEqualsCreatedTask() {
        // проверьте, что экземпляры класса Task равны друг другу, если равен их id;
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW, 20, time1.plusDays(4));
        taskManager.createTask(task1);
        Task getTask = taskManager.getByTaskId(task1.getId());
        assertEquals(task1, getTask);
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
        Subtask subtask1ForEpic1 = new Subtask(epic1Created.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 21, time1.plusDays(5));
        Subtask savedSubtask = taskManager.createSubtask(subtask1ForEpic1);
        assertEquals(subtask1ForEpic1, savedSubtask);
    }

    @Test
    void objectEpicCanNotMakePersonalSubtask() {
        // // проверьте, что объект Subtask нельзя сделать своим же эпиком
        Subtask subtask1 = new Subtask(9, "Купить: ",
                "пластик. посуду ", Status.NEW, 22, time1.plusDays(6));
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.createSubtask(subtask1));
    }

    @Test
    void inMemoryTaskManagerAddTaskSubtaskEpic() {
        //Проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW, 23, time1.plusDays(7));
        Task savedTask = taskManager.createTask(task1);
        assertEquals(task1.getId(), savedTask.getId());

        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);
        assertEquals(epic1.getId(), savedEpic.getId());

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 24, time1.plusDays(8));
        Subtask saveSubtask1 = taskManager.createSubtask(subtask1ForEpic1);
        assertEquals(subtask1ForEpic1.getId(), saveSubtask1.getId());
        Subtask subtask2ForEpic1 = new Subtask(savedEpic.getId(), "Не забыть: ",
                "палатку, пенки", Status.NEW, 25, time1.plusDays(9));
        Subtask saveSubtask2 = taskManager.createSubtask(subtask2ForEpic1);
        assertEquals(subtask2ForEpic1.getId(), saveSubtask2.getId());
    }

    @Test
    void checkIdConflict() {
        // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
        Task task1 = new Task("таск1.Имя", "таск1.Описание",
                Status.NEW, 26, time1.plusDays(10));
        taskManager.createTask(task1);
        Task task2 = new Task(0, "таск2.Имя", "таск2.Описание",
                Status.NEW, 26, time1.plusDays(11));
        taskManager.createTask(task2);
        assertEquals(taskManager.getTasks().size(), 2);
    }

    @Test
    void taskFieldImmutability() {
        // создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
        Task task1 = new Task("таск1.Имя", "таск1.Описание",
                Status.NEW, 27, time1.plusDays(12));
        Task savedTask1 = taskManager.createTask(task1);
        Task task2 = new Task("таск2.Имя", "таск2.Описание",
                Status.NEW, 28, time1.plusDays(13));
        Task savedTask2 = taskManager.createTask(task2);
        Task task3 = new Task("таск3.Имя", "таск3.Описание",
                Status.NEW, 29, time1.plusDays(14));
        Task savedTask3 = taskManager.createTask(task3);
        assertEquals(savedTask1.getId(), task1.getId());
        assertEquals(savedTask2.getId(), task2.getId());
        assertEquals(savedTask3.getId(), task3.getId());
    }

    @Test
    void checkSaveLastVersionHistoryManager() {
        // убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
        Task task1 = new Task("таск1.Имя", "таск1.Описание",
                Status.NEW, 30, time1.plusDays(15));
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

    @Test
    void checkDeleteIdSubtask() {
        // Удаляемые подзадачи не должны хранить внутри себя старые id.
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 31, time1.plusDays(16));
        Subtask saveSubtask1 = taskManager.createSubtask(subtask1ForEpic1);

        assertTrue(taskManager.deleteSubtask(saveSubtask1.getId()));
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getBySubtaskId(saveSubtask1.getId()));
        assertEquals(saveSubtask1.getEpicId(), 0);
    }

    @Test
    void checkDeleteSubtaskForEpic() {
        //  Внутри эпиков не должно оставаться неактуальных id подзадач.
        Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
        Epic savedEpic = taskManager.createEpic(epic1);

        Subtask subtask1ForEpic1 = new Subtask(savedEpic.getId(), "Купить: ",
                "пластик. посуду ", Status.NEW, 32, time1.plusDays(18));
        Subtask saveSubtask1 = taskManager.createSubtask(subtask1ForEpic1);

        assertTrue(taskManager.deleteSubtask(saveSubtask1.getId()));

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getBySubtaskId(saveSubtask1.getId()));
        List<Subtask> subtaskInEpic = savedEpic.getSubtasks();

        Integer subtask = 0;
        if (subtaskInEpic.isEmpty()) {
            subtask = null;
        }
        assertNull(subtask);
    }

    @Test
    void checkChangeSetter() {
        // С помощью сеттеров экземпляры задач позволяют изменить любое своё поле, но это может повлиять на данные внутри менеджера.
        Task task1 = new Task("таск1.Имя", "таск1.Описание", Status.NEW, 33, time1.plusDays(19));
        Task savedTask1 = taskManager.createTask(task1);
        task1.setId(9);
        assertEquals(savedTask1.getId(), 9);
    }

}