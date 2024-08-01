package ru.practicum.task_tracker.manager;

import ru.practicum.task_tracker.CSVFormatter;
import ru.practicum.task_tracker.ManagerSaveException;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String HOME = System.getProperty("user.dir");
    private static final String fileName = "tasksFile.csv";
    private static final Path TASKS_FILE_PATH = Paths.get(HOME, fileName);

    public static void loadFromFile(TaskManager taskManager) {

        try {
            List<String> lines = Files.readAllLines(TASKS_FILE_PATH);
            List<Integer> allIds = new ArrayList<>();

            for (int i = 1; i < lines.size(); i++) {
                String row = lines.get(i);
                int commaIndex = row.indexOf(",");

                Task task = switch (row.substring(0, commaIndex)) {
                    case "TASK" -> taskManager.createTask(CSVFormatter.taskFromString(row));
                    case "EPIC" -> taskManager.createEpic(CSVFormatter.epicFromString(row));
                    case "SUBTASK" -> taskManager.createSubtask(CSVFormatter.subtaskFromString(row));
                    default -> null;
                };
                allIds.add(task.getId());
            }
            if (!allIds.isEmpty()) {
                taskManager.setNextId(Collections.max(allIds));
            }
        } catch (IOException e) {
            throw ManagerSaveException.loadException(e);
        }
    }

    private void save() {

        try {
            if (TASKS_FILE_PATH.getFileName() == null) {
                Files.createFile(TASKS_FILE_PATH);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(CSVFormatter.getHeader());
            bw.newLine();

            List<Task> tasks = super.getTasks();
            List<Epic> epics = super.getEpics();
            List<Subtask> subtasks = super.getSubtasks();
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    bw.write("TASK,");
                    bw.write(CSVFormatter.toString(task));
                    bw.newLine();
                }
            }
            if (!epics.isEmpty()) {
                for (Epic epic : epics) {
                    bw.write("EPIC,");
                    bw.write(CSVFormatter.toString(epic));
                    bw.newLine();
                }
            }
            if (!subtasks.isEmpty()) {
                for (Subtask subtask : subtasks) {
                    bw.write("SUBTASK,");
                    bw.write(CSVFormatter.toString(subtask));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw ManagerSaveException.saveException(e);
        }
    }

    @Override
    public Task createTask(Task task) {
        Task createTask = super.createTask(task);
        save();
        return createTask;
    }

    @Override
    public Task updateTask(Task task) {
        Task updateTask = super.updateTask(task);
        save();
        return updateTask;
    }

    @Override
    public boolean deleteTask(int taskId) {
        boolean deleteTask = super.deleteTask(taskId);
        save();
        return deleteTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createEpic = super.createEpic(epic);
        save();
        return createEpic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updateEpic = super.updateEpic(epic);
        save();
        return updateEpic;
    }

    @Override
    public boolean deleteEpic(int epicId) {
        boolean deleteEpic = super.deleteEpic(epicId);
        save();
        return deleteEpic;
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        super.updateStatusEpic(epic);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createSubtask = super.createSubtask(subtask);
        save();
        return createSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updateSubtask = super.updateSubtask(subtask);
        save();
        return updateSubtask;
    }

    @Override
    public boolean deleteSubtask(int subtaskId) {
        boolean deleteSubtask = super.deleteSubtask(subtaskId);
        save();
        return deleteSubtask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
}
