package ru.practicum.task_tracker.http.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.task_tracker.http.Exception.BadRequestException;
import ru.practicum.task_tracker.http.Exception.NotAcceptableException;
import ru.practicum.task_tracker.http.Exception.NotFoundException;

import java.io.IOException;


public class ErrorHandler extends BaseHttpHandler {

    public static void handleException(HttpExchange h, Exception e) throws IOException {
        int status;

        try {
            status = switch (e) {
                case BadRequestException badRequestException -> 400;
                case NotFoundException notFoundException -> 404;
                case NotAcceptableException notAcceptableException -> 406;
                case null, default -> throw e;
            };
            sendException(h, e.getMessage(), status);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}