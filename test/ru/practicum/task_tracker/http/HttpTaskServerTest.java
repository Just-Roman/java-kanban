package ru.practicum.task_tracker.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.task_tracker.manager.InMemoryTaskManager;
import ru.practicum.task_tracker.manager.TaskManager;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpTaskServerTest {
    LocalDateTime time1 = LocalDateTime.of(2020, 1, 1, 13, 0);
    Task task1NotId = new Task("таск1.Имя", "таск1.Описание", Status.NEW, 23, time1);
    Task task1ThrowName = new Task(null, "таск1.Описание", Status.NEW, 23, time1);
    Task task1ThrowStartTime = new Task("таск1.Имя", "таск1.Описание", Status.NEW, 23,
            time1.plusMinutes(5));
    Task task2 = new Task(1, "таск2.Имя", "таск2.Описание",
            Status.NEW, 23, time1.plusDays(1));
    Task task1Id = new Task(0, "таск1.Имя", "таск1.Описание", Status.DONE, 23, time1);
    Epic epic1 = new Epic("Поход в горы", "обязательно с друзьями");
    Epic epic1ThrowName = new Epic(null, "обязательно с друзьями");
    Subtask subtask1ForEpic1 = new Subtask(0, "Купить: ",
            "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));
    Subtask subtask1ForEpic1ThrowName = new Subtask(0, null,
            "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));
    Subtask subtask1ForEpic1ThrowStartTime = new Subtask(0, "Купить: ", "пластик. посуду ",
            Status.NEW, 12, time1.plusDays(2).plusMinutes(5));
    Subtask subtask2ForEpic1 = new Subtask(0, 3, "Купить: ",
            "пластик. посуду ", Status.NEW, 12, time1.plusDays(2));

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    private HttpResponse<String> getResponsePost(String json, String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getResponseGet(String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getResponseDelete(String path) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // конвертируем её в JSON
        String taskJson = gson.toJson(task1NotId);
        String path = "http://localhost:8080/tasks";
        HttpResponse<String> response = getResponsePost(taskJson, path);
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("таск1.Имя", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

//        400 Bad Request
        String taskThrowJson = gson.toJson(task1ThrowName);
        HttpResponse<String> responseThrows = getResponsePost(taskThrowJson, path);
        assertEquals(400, responseThrows.statusCode());

//        406 Not Acceptable
        HttpResponse<String> responseTh = getResponsePost(taskJson, path);
        assertEquals(406, responseTh.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        testAddTask();
        String taskJson = gson.toJson(task1Id);
        String path = "http://localhost:8080/tasks";
        HttpResponse<String> response = getResponsePost(taskJson, path);
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("таск1.Имя", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

//          400 Bad Request
        String taskThrowJson1 = gson.toJson(task1ThrowName);
        HttpResponse<String> responseThrows1 = getResponsePost(taskThrowJson1, path);
        assertEquals(400, responseThrows1.statusCode());

//        404 Not Found
        String taskThrowJson2 = gson.toJson(task2);
        HttpResponse<String> responseThrows2 = getResponsePost(taskThrowJson2, path);
        assertEquals(404, responseThrows2.statusCode());

//        406 Not Acceptable
        String taskThrowJson3 = gson.toJson(task1ThrowStartTime);
        HttpResponse<String> responseThrows3 = getResponsePost(taskThrowJson3, path);
        assertEquals(406, responseThrows3.statusCode());
    }

    @Test
    public void getTasks() throws IOException, InterruptedException {
        testAddTask();
        String path = "http://localhost:8080/tasks";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("таск1.Имя", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void getTaskById() throws IOException, InterruptedException {
        testAddTask();
        String path = "http://localhost:8080/tasks/0";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("таск1.Имя", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        //        404 Not Found
        String pathThrow = "http://localhost:8080/tasks/1";
        HttpResponse<String> responseThrow = getResponseGet(pathThrow);
        assertEquals(404, responseThrow.statusCode());
    }

    @Test
    public void deleteTaskById() throws IOException, InterruptedException {
        testAddTask();
        String path = "http://localhost:8080/tasks/0";
        HttpResponse<String> response = getResponseDelete(path);
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(0, tasksFromManager.size(), "Некорректное количество задач");

        //        404 Not Found
        String pathThrow = "http://localhost:8080/tasks/1";
        HttpResponse<String> responseThrow = getResponseGet(pathThrow);
        assertEquals(404, responseThrow.statusCode());
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        String epicJson = gson.toJson(epic1);
        String path = "http://localhost:8080/epics";
        HttpResponse<String> response = getResponsePost(epicJson, path);
        assertEquals(201, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Поход в горы", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");

        //        400 Bad Request
        String epicThrowJson = gson.toJson(epic1ThrowName);
        HttpResponse<String> responseThrows = getResponsePost(epicThrowJson, path);
        assertEquals(400, responseThrows.statusCode());
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        testAddEpic();
        String path = "http://localhost:8080/epics";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Поход в горы", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetByIdEpic() throws IOException, InterruptedException {
        testAddEpic();
        String path = "http://localhost:8080/epics/0";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Поход в горы", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");


        //        404 Not Found
        String pathThrow = "http://localhost:8080/epics/1";
        HttpResponse<String> responseThrow = getResponseGet(pathThrow);
        assertEquals(404, responseThrow.statusCode());
    }

    @Test
    public void testGetByIdEpicAllSubtasks() throws IOException, InterruptedException {
        testAddSubtask();
        String path = "http://localhost:8080/epics/0/subtasks";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());
        List<Subtask> epicsFromManager = manager.getSubtasks();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("Купить: ", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");

        //        404 Not Found
        String pathThrow = "http://localhost:8080/epics/1/subtasks";
        HttpResponse<String> responseThrow = getResponseGet(pathThrow);
        assertEquals(404, responseThrow.statusCode());
    }

    @Test
    public void testDeleteByIdEpic() throws IOException, InterruptedException {
        testAddEpic();
        String path = "http://localhost:8080/epics/0";
        HttpResponse<String> response = getResponseDelete(path);
        assertEquals(200, response.statusCode());
        List<Epic> epicsFromManager = manager.getEpics();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(0, epicsFromManager.size(), "Некорректное количество задач");

        //        404 Not Found
        String pathThrow = "http://localhost:8080/epics/1";
        HttpResponse<String> responseThrow = getResponseGet(pathThrow);
        assertEquals(404, responseThrow.statusCode());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        testAddEpic();

        String subtaskJson = gson.toJson(subtask1ForEpic1);
        String path = "http://localhost:8080/subtasks";
        HttpResponse<String> response = getResponsePost(subtaskJson, path);
        assertEquals(201, response.statusCode());
        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Купить: ", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

//        400 Bad Request
        String taskThrowJson1 = gson.toJson(subtask1ForEpic1ThrowName);
        HttpResponse<String> responseThrows1 = getResponsePost(taskThrowJson1, path);
        assertEquals(400, responseThrows1.statusCode());

//        406 Not Acceptable  StartTime
        String taskThrowJson2 = gson.toJson(subtask1ForEpic1ThrowStartTime);
        HttpResponse<String> responseThrows2 = getResponsePost(taskThrowJson2, path);
        assertEquals(406, responseThrows2.statusCode());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        testAddSubtask();
        Subtask subtask = manager.getBySubtaskId(1);
        String subtaskJson = gson.toJson(subtask);
        String path = "http://localhost:8080/subtasks";
        HttpResponse<String> response = getResponsePost(subtaskJson, path);
        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Купить: ", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        //        400 Bad Request
        String taskThrowJson1 = gson.toJson(subtask1ForEpic1ThrowName);
        HttpResponse<String> responseThrows1 = getResponsePost(taskThrowJson1, path);
        assertEquals(400, responseThrows1.statusCode());

        //        404 Not Found
        String taskThrowJson2 = gson.toJson(subtask2ForEpic1);
        HttpResponse<String> responseThrows2 = getResponsePost(taskThrowJson2, path);
        assertEquals(404, responseThrows2.statusCode());

//        406 Not Acceptable  StartTime
        String taskThrowJson3 = gson.toJson(subtask1ForEpic1ThrowStartTime);
        HttpResponse<String> responseThrows3 = getResponsePost(taskThrowJson3, path);
        assertEquals(406, responseThrows3.statusCode());
    }

    @Test
    public void testGeSubtask() throws IOException, InterruptedException {
        testAddSubtask();
        String path = "http://localhost:8080/subtasks";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Купить: ", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetByIdSubtask() throws IOException, InterruptedException {
        testAddSubtask();
        String path = "http://localhost:8080/subtasks/1";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Купить: ", subtasksFromManager.getFirst().getName(), "Некорректное имя задачи");


        //        404 Not Found
        String taskThrowJson2 = gson.toJson(subtask2ForEpic1);
        HttpResponse<String> responseThrows2 = getResponsePost(taskThrowJson2, path);
        assertEquals(404, responseThrows2.statusCode());
    }

    @Test
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        testAddSubtask();
        String path = "http://localhost:8080/subtasks/1";
        HttpResponse<String> response = getResponseDelete(path);
        assertEquals(200, response.statusCode());
        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(0, subtasksFromManager.size(), "Некорректное количество задач");

        //        404 Not Found
        String taskThrowJson2 = gson.toJson(subtask2ForEpic1);
        HttpResponse<String> responseThrows2 = getResponsePost(taskThrowJson2, path);
        assertEquals(404, responseThrows2.statusCode());
    }

    @Test
    public void testGeHistory() throws IOException, InterruptedException {
        testAddSubtask();
        testAddTask();
        String path = "http://localhost:8080/history";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());
        List<Task> subtasksFromManager = manager.getHistory();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(3, subtasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    public void testGePrioritized() throws IOException, InterruptedException {
        testAddSubtask();
        testAddTask();
        String path = "http://localhost:8080/prioritized";
        HttpResponse<String> response = getResponseGet(path);
        assertEquals(200, response.statusCode());

        List<Task> subtasksFromManager = new ArrayList<>(manager.getPrioritizedTasks());
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals(subtasksFromManager.getFirst().getName(), task1Id.getName());
        assertEquals(subtasksFromManager.getLast().getName(), "Купить: ");
    }

}