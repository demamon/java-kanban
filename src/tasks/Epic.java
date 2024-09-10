package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.status = TaskStatus.NEW;
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.status = TaskStatus.NEW;
    }

    public Epic(String name, String description, TaskStatus taskStatus, int id) {
        super(name, description, taskStatus, id);
    }

    public Epic(String name, String description, TaskStatus taskStatus, int id, LocalDateTime startTime,
                Duration duration, LocalDateTime endTime) {
        super(name, description, taskStatus, id, startTime, duration);
        this.endTime = endTime;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void deleteSubtaskId(Integer idSubtask) {
        subtaskIds.remove(idSubtask);
    }

    public void clearSubtaskId() {
        subtaskIds.clear();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
        return id + "," + TaskType.EPIC + "," + name + "," + status + "," + description + "," + stringStartTime + ","
                + stringDuration + "," + endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic epic)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }
}
