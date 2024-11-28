package ru.practicum.task_tracker.http.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.task_tracker.task.Status;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected static void sendException(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.getResponseBody().write(resp);
        h.close();
    }


    protected Integer jsonObjectGetId(String body) {
        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
        return jsonObject.get("id") != null ? jsonObject.get("id").getAsInt() : null;
    }

    protected Status getStatus(String str) {
        return switch (str) {
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default -> Status.NEW;
        };
    }

    protected Integer getIdFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            return Integer.parseInt(parts[2]);
        }
        return null;
    }

}
