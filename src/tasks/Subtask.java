package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId, Duration duration) {
        super(name, description, status, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int id, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, id, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int id, int epicId, Duration duration) {
        super(name, description, status, id, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int id, int epicId) {
        super(name, description, status, id);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String stringStartTime;
        String stringDuration;
        if (startTime == null) {
            stringStartTime = "Время начала не задано";
        } else {
            stringStartTime = startTime.toString();
        }
        if (duration == null) {
            stringDuration = "0";
        } else {
            stringDuration = String.valueOf(duration.toMinutes());
        }
        return id + "," + TaskType.SUBTASK + "," + name + "," + status + "," + description + "," + epicId + "," +
                stringStartTime + "," + stringDuration + "," + getEndTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask subtask)) return false;
        if (!super.equals(o)) return false;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
