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
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String path = exchange.getRequestURI().getPath();
            Integer id = getIdFromPath(path);

            switch (exchange.getRequestMethod()) {
                case "GET":
                    if (id == null) {
                        List<Epic> epics = taskManager.getEpics();
                        String response = HttpTaskServer.getGson().toJson(epics);
                        sendText(exchange, response, 200);
                    } else {
                        Epic epic = taskManager.getByEpicId(id);
                        System.out.println(epic.getDuration());

                        if (booleanSubtasksPath(path)) {
                            List<Subtask> subtasks = epic.getSubtasks();
                            String response = HttpTaskServer.getGson().toJson(subtasks);
                            sendText(exchange, response, 200);
                        }
                        String response = HttpTaskServer.getGson().toJson(epic);
                        sendText(exchange, response, 200);
                    }
                    break;
                case "POST":
                    Epic createdEpic = taskManager.createEpic(getNewEpic(body));
                    String responsePost = HttpTaskServer.getGson().toJson("Задача создана. id = " + createdEpic.getId());
                    sendText(exchange, responsePost, 201);
                    break;
                case "DELETE":
                    if (id == null) {
                        throw new NotAcceptableException("Не передан id.");
                    }
                    taskManager.deleteEpic(id);
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

    private Epic getNewEpic(String body) {
        JsonElement jsonElement = JsonParser.parseString(body);

        if (jsonElement.isJsonNull()) {
            throw new BadRequestException("Нет входных параметров");
        }

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String name = jsonObject.get("name") != null ? jsonObject.get("name").getAsString() : null;
        String description = jsonObject.get("description") != null ?
                jsonObject.get("description").getAsString() : null;

        if (name == null || description == null) {
            throw new BadRequestException("От пользователя некорректно передана задача");
        } else {
            return new Epic(name, description);
        }

    }

    protected Boolean booleanSubtasksPath(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 4) {
            return parts[3].equals("subtasks");
        }
        return false;

    }

}
