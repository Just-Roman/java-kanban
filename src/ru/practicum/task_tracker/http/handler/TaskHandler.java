package ru.practicum.task_tracker.http.handler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.task_tracker.http.Exception.BadRequestException;
import ru.practicum.task_tracker.http.Exception.NotAcceptableException;
import ru.practicum.task_tracker.http.HttpTaskServer;
import ru.practicum.task_tracker.manager.TaskManager;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Integer id = getIdFromPath(exchange.getRequestURI().getPath());

            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (id == null) {
                        List<Task> tasks = taskManager.getTasks();
                        String response = HttpTaskServer.getGson().toJson(tasks);
                        sendText(exchange, response, 200);
                    } else {
                        Task task = taskManager.getByTaskId(id);
                        String response = HttpTaskServer.getGson().toJson(task);
                        sendText(exchange, response, 200);
                    }
                    break;
                case "POST":
                    Integer taskId = jsonObjectGetId(body);
                    if (taskId == null) {
                        Task createdTask = taskManager.createTask(getNewTask(body));
                        String response = HttpTaskServer.getGson().toJson("Задача создана.  id = " + createdTask.getId());
                        sendText(exchange, response, 201);
                    } else {
                        Task task = getNewTask(body);
                        task.setId(taskId);
                        taskManager.updateTask(task);
                        String response = HttpTaskServer.getGson().toJson("Задача обновлена.");
                        sendText(exchange, response, 200);
                    }
                    break;
                case "DELETE":
                    if (id == null) {
                        throw new NotAcceptableException("Не передан id.");
                    }
                    taskManager.deleteTask(id);
                    String response = HttpTaskServer.getGson().toJson("Задача удалена.");
                    sendText(exchange, response, 200);
                    break;
                default:
                    throw new NotAcceptableException("Нет обработки текущей команды.");
            }
        } catch (Exception e) {
            ErrorHandler.handleException(exchange, e);
        } finally {
            exchange.close();
        }
    }

    private Task getNewTask(String body) {
        JsonElement jsonElement = JsonParser.parseString(body);

        if (jsonElement.isJsonNull()) {
            throw new BadRequestException("Нет входных параметров");
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name") != null ? jsonObject.get("name").getAsString() : null;
        String description = jsonObject.get("description") != null ?
                jsonObject.get("description").getAsString() : null;
        Status status = getStatus(jsonObject.get("status") != null ?
                jsonObject.get("status").getAsString() : "null");
        Integer duration = jsonObject.get("duration") != null ? jsonObject.get("duration").getAsInt() : null;
        String startTimeString = jsonObject.get("startTime") != null ?
                jsonObject.get("startTime").getAsString() : "null";
        LocalDateTime startTime = LocalDateTime.parse(startTimeString);
//        Gson gson = new GsonBuilder().create();
//        LocalDateTime startTime = HttpTaskServer.getGson().fromJson(startTimeString, LocalDateTime.class);
//        LocalDateTime startTime = gson.fromJson(startTimeString, new LocalDateTimeToken().getType());

        if (name == null || duration == null) {
            throw new BadRequestException("От пользователя некорректно передана задача");
        } else {
            return new Task(name, description, status, duration, startTime);
        }
    }


}

