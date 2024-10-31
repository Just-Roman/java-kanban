package ru.practicum.task_tracker.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }


    public Epic(Integer id, String name, String description, Status status, long duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Subtask subtask) {
        if (subtask.getId() == this.getId()) {
            return;
        }
        subtasks.add(subtask);
        setDurationAndLocalDateTime();
    }

    private void setDurationAndLocalDateTime() {

        super.startTime = subtasks.stream()
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);


        super.duration = Duration.ofMinutes(subtasks.stream()
                .map(Task::getDuration)
                .reduce((long) 0, Long::sum)
        );

    }

    public LocalDateTime getEndTime() {
        return subtasks.stream()
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public void clearsubtaskIds() {
        subtasks.clear();
    }

    public void removesubtaskById(Subtask subtask) {
        subtasks.remove(subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() + '\'' +
                ", subtasks=" + subtasks + '\'' +
                ", duration=" + duration + '\'' +
                ", startTime=" + startTime +
                '}' + "\n";
    }

}
