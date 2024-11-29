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
import ru.practicum.task_tracker.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        String response = HttpTaskServer.getGson().toJson(subtasks);
                        sendText(exchange, response, 200);
                    } else {
                        Subtask subtask = taskManager.getBySubtaskId(id);
                        String response = HttpTaskServer.getGson().toJson(subtask);
                        sendText(exchange, response, 200);
                    }
                    break;
                case "POST":
                    Integer subtaskId = jsonObjectGetId(body);
                    if (subtaskId == null) {
                        Subtask createdSubtask = taskManager.createSubtask(getNewSubtask(body));
                        String response = HttpTaskServer.getGson().toJson("Задача создана.  id = " + createdSubtask.getId());
                        sendText(exchange, response, 201);
                    } else {
                        Subtask subtask = getNewSubtask(body);
                        subtask.setId(subtaskId);
                        taskManager.updateSubtask(subtask);
                        String response = HttpTaskServer.getGson().toJson("Задача обновлена.");
                        sendText(exchange, response, 200);
                    }
                    break;
                case "DELETE":
                    if (id == null) {
                        throw new NotAcceptableException("Не передан id.");
                    }
                    taskManager.deleteSubtask(id);
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

    private Subtask getNewSubtask(String body) {
        JsonElement jsonElement = JsonParser.parseString(body);

        if (jsonElement.isJsonNull()) {
            throw new BadRequestException("Нет входных параметров");
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Integer epicId = jsonObject.get("epicId") != null ? jsonObject.get("epicId").getAsInt() : null;
        String name = jsonObject.get("name") != null ? jsonObject.get("name").getAsString() : null;
        String description = jsonObject.get("description") != null ?
                jsonObject.get("description").getAsString() : null;
        Status status = getStatus(jsonObject.get("status") != null ?
                jsonObject.get("status").getAsString() : "null");
        Integer duration = jsonObject.get("duration") != null ? jsonObject.get("duration").getAsInt() : null;
        String startTimeString = jsonObject.get("startTime") != null ?
                jsonObject.get("startTime").getAsString() : "null";
        LocalDateTime startTime = LocalDateTime.parse(startTimeString);

        if (epicId == null || name == null || duration == null) {
            throw new BadRequestException("От пользователя некорректно передана задача");
        } else {
            return new Subtask(epicId, name, description, status, duration, startTime);
        }

    }


}
