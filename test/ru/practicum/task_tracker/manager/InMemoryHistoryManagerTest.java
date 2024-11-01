package ru.practicum.task_tracker.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.Managers;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();
    LocalDateTime time1 = LocalDateTime.of(2020, 1, 1, 13, 0);

    @BeforeEach
    void setUp() {
        historyManager.removeAll();
    }

    @Test
    void add() {
        Task task1 = new Task(0, "таск1.Имя", "таск1.Описание", Status.NEW, 23, time1);
        historyManager.add(task1);
        List<Task> listTasks = historyManager.getHistory();
        assertEquals(listTasks.size(), 1);
        assertEquals(listTasks.getFirst(), task1);
    }

    @Test
    void remove() {
        Task task1 = new Task(0, "таск1.Имя", "таск1.Описание", Status.NEW, 23, time1);
        assertFalse(historyManager.remove(task1.getId()));
        historyManager.add(task1);
        assertTrue(historyManager.remove(task1.getId()));
    }

    @Test
    void removeAll() {
        Task task1 = new Task(0, "таск1.Имя", "таск1.Описание", Status.NEW, 23, time1);
        Task task2 = new Task(1, "таск2.Имя", "таск2.Описание",
                Status.NEW, 23, time1.plusDays(2));
        historyManager.add(task1);
        historyManager.add(task2);
        assertEquals(historyManager.getHistory().size(), 2);
        historyManager.removeAll();
        assertEquals(historyManager.getHistory().size(), 0);
    }

    @Test
    void getHistory() {
        Task task1 = new Task(0, "таск1.Имя", "таск1.Описание", Status.NEW, 23, time1);
        Task task2 = new Task(1, "таск2.Имя", "таск2.Описание",
                Status.NEW, 23, time1.plusDays(1));
        Task task3 = new Task(2, "таск3.Имя", "таск3.Описание",
                Status.NEW, 23, time1.plusDays(2));

//        Пустая история задач
        assertEquals(historyManager.getHistory().size(), 0);

//        Дублирование (по одинаковым id невозможна)
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> listTasks = historyManager.getHistory();
        assertEquals(listTasks.size(), 1);
        assertEquals(listTasks.getFirst(), task1);

//        Удаление из истории: начало, середина, конец.
//        Начало
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());
        List<Task> listTasks2 = historyManager.getHistory();
        assertEquals(listTasks2.size(), 2);
        assertEquals(listTasks2.getFirst(), task2);
        assertEquals(listTasks2.getLast(), task3);

//        Середина
        historyManager.add(task1);
        historyManager.remove(task3.getId());
        List<Task> listTasks3 = historyManager.getHistory();
        assertEquals(listTasks3.size(), 2);
        assertEquals(listTasks3.getFirst(), task2);
        assertEquals(listTasks3.getLast(), task1);

//        Kонец
        historyManager.add(task3);
        historyManager.remove(task3.getId());
        List<Task> listTasks4 = historyManager.getHistory();
        assertEquals(listTasks4.size(), 2);
        assertEquals(listTasks4.getFirst(), task2);
        assertEquals(listTasks4.getLast(), task1);
    }
}