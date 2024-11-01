package ru.practicum.task_tracker;

import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Status;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.time.LocalDateTime;

public class CSVFormatter {

    private CSVFormatter() {

    }

    public static String toString(Task task) {
        return new StringBuilder()
                .append(task.getId()).append(",")
                .append(task.getName()).append(",")
                .append(task.getDescription()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDuration()).append(",")
                .append(task.getStartTime()).append(",")
                .append(task.getEpicId()).append(",")
                .toString();
    }

    public static Task taskFromString(String csvRow) {
        String[] columns = csvRow.split(",");
        return new Task(Integer.parseInt(columns[1]), columns[2], columns[3], getStatus(columns[4]),
                Long.parseLong(columns[5]), LocalDateTime.parse(columns[6]));
    }

    public static Epic epicFromString(String csvRow) {
        String[] columns = csvRow.split(",");
        return new Epic(Integer.parseInt(columns[1]), columns[2], columns[3], getStatus(columns[7]));
    }

    public static Subtask subtaskFromString(String csvRow) {
        String[] columns = csvRow.split(",");
        return new Subtask(Integer.parseInt(columns[7]), Integer.parseInt(columns[1]), columns[2],
                columns[3], getStatus(columns[4]), Long.parseLong(columns[5]), LocalDateTime.parse(columns[6]));
    }

    private static Status getStatus(String str) {
        return switch (str) {
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default -> Status.NEW;
        };
    }

    public static String getHeader() {
        return "type,id,name,description,status,epicId";
    }
}
