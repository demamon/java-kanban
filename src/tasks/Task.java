package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {

    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int id;
    protected Duration duration;
    protected LocalDateTime startTime;


    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, int id, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, TaskStatus status, int id, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.duration = duration;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public Task(String name, String description, TaskStatus taskStatus, int id) {
        this.name = name;
        this.description = description;
        this.status = taskStatus;
        this.id = id;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        if (duration == null) {
            return Duration.ZERO;
        }
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (this.name == null) {
            this.name = name;
            return;
        }
        System.out.println("У задачи уже есть name");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (this.description == null) {
            this.description = description;
            return;
        }
        System.out.println("У задачи уже есть description");
    }

    public int getId() {
        return id;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setId(int id) {
        if (this.id == 0) {
            this.id = id;
            return;
        }
        System.out.println("У задачи уже есть id");
    }

    public TaskStatus getStatus() {
        return status;
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
        return id + "," + TaskType.TASK + "," + name + "," + status + "," + description + "," + stringStartTime + "," +
                stringDuration + "," + getEndTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description,
                task.description) && status == task.status && Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, duration, startTime);
    }


    @Override
    public int compareTo(Task o) {

        if (startTime.isAfter(o.startTime)) {
            return 1;
        }
        if (startTime.equals(o.startTime)) {
            return 0;
        }
        return -1;
    }
}

