package ru.practicum.task_tracker.task;

import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(int epicId, String name, String description, Status status, long duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public Subtask(int epicId, Integer id, String name, String description, Status status, long duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + this.getId() +
                ", name='" + this.getName() + '\'' +
                ", description='" + this.getDescription() + '\'' +
                ", status=" + this.getStatus() + '\'' +
                ", epicId=" + epicId + '\'' +
                ", duration=" + duration + '\'' +
                ", startTime=" + startTime +
                '}' + "\n";
    }
}
