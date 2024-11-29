package ru.practicum.task_tracker.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.task_tracker.http.Exception.NotAcceptableException;
import ru.practicum.task_tracker.http.HttpTaskServer;
import ru.practicum.task_tracker.manager.TaskManager;
import ru.practicum.task_tracker.task.Task;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            if (exchange.getRequestMethod().equals("GET")) {
                Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String response = HttpTaskServer.getGson().toJson(prioritizedTasks);
                sendText(exchange, response, 200);
            } else {
                throw new NotAcceptableException("Нет обработки текущей команды.");
            }
        } catch (Exception e) {
            ErrorHandler.handleException(exchange, e);
        } finally {
            exchange.close();
        }
    }
}
