package ru.practicum.task_tracker.manager;

import ru.practicum.task_tracker.CSVFormatter;
import ru.practicum.task_tracker.ManagerSaveException;
import ru.practicum.task_tracker.task.Epic;
import ru.practicum.task_tracker.task.Subtask;
import ru.practicum.task_tracker.task.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private static File file;

    FileBackedTaskManager(File file) {
        FileBackedTaskManager.file = file;
    }

    public FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(path);
            List<Integer> allIds = new ArrayList<>();

            for (int i = 1; i < lines.size(); i++) {
                String row = lines.get(i);
                int commaIndex = row.indexOf(",");

                switch (row.substring(0, commaIndex)) {
                    case "TASK":
                        Task taskNew = CSVFormatter.taskFromString(row);
                        int taskId = taskNew.getId();
                        tasks.put(taskId, taskNew);
                        allIds.add(taskId);
                        break;
                    case "EPIC":
                        Epic epicNew = CSVFormatter.epicFromString(row);
                        int epicId = epicNew.getId();
                        epics.put(epicId, epicNew);
                        allIds.add(epicId);
                        break;
                    case "SUBTASK":
                        Subtask subtaskNew = CSVFormatter.subtaskFromString(row);
                        int subtaskId = subtaskNew.getId();
                        subtasks.put(subtaskId, subtaskNew);
                        allIds.add(subtaskId);
                        break;
                }
            }
            if (!allIds.isEmpty()) {
                nextId = Collections.max(allIds);
            }
        } catch (IOException e) {
            throw ManagerSaveException.loadException(e);
        }
        return fileBackedTaskManager;
    }

    private void save() {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getName()))) {

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
