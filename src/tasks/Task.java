package tasks;

import java.util.Objects;

public class Task {

    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int id;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
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
        return "tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        int reuslt = Objects.hash(name, description, status);
        return reuslt = 31 * reuslt + Objects.hash(id);
    }
}

