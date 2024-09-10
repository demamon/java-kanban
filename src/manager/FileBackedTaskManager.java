package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    private void save() {
        try {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(path),
                    StandardCharsets.UTF_8))) {
                fileWriter.write("id,type,name,status,description,startTime,duration,endTime,epic" + "\n");
                super.tasks.values().forEach(task -> {
                    try {
                        fileWriter.write(task.toString() + "\n");
                    } catch (IOException e) {
                        throw new ManagerSaveException("Произошла ошибка во время записи файла.");
                    }
                });
                super.epics.values().forEach(epic -> {
                    try {
                        fileWriter.write(epic.toString() + "\n");
                    } catch (IOException e) {
                        throw new ManagerSaveException("Произошла ошибка во время записи файла.");
                    }
                });
                super.subtasks.values().forEach(subtask -> {
                    try {
                        fileWriter.write(subtask.toString() + "\n");
                    } catch (IOException e) {
                        throw new ManagerSaveException("Произошла ошибка во время записи файла.");
                    }
                });
            } catch (IOException e) {
                throw new ManagerSaveException("Произошла ошибка во время записи файла.");
            }
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager tm = new FileBackedTaskManager(path);
        int maxId = 0;
        try {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(String.valueOf(path),
                    StandardCharsets.UTF_8))) {
                fileReader.readLine();
                while (fileReader.ready()) {
                    String task = fileReader.readLine();
                    String[] taskItem = task.split(",");
                    if (taskItem[1].equals(TaskType.TASK.toString())) {
                        maxId = tm.taskFromString(taskItem, maxId);
                    } else if (taskItem[1].equals(TaskType.EPIC.toString())) {
                        maxId = tm.epicFromString(taskItem, maxId);
                    } else if (taskItem[1].equals(TaskType.SUBTASK.toString())) {
                        maxId = tm.subtaskFromString(taskItem, maxId);
                    }
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Произошла ошибка во время чтения файла.");
            }

        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
        }
        tm.setGenerationId(maxId);
        return tm;
    }

    private int taskFromString(String[] taskItem, int maxId) {
        Task task;
        int id = Integer.parseInt(taskItem[0]);
        String startTime = taskItem[5];
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Duration duration = Duration.ofMinutes(Long.parseLong(taskItem[6]));
        if (startTime.equals("Время начала не задано")) {
            task = new Task(taskItem[2], taskItem[4], taskStatus, id, duration);
        } else {
            LocalDateTime localDateTime = LocalDateTime.parse(taskItem[5]);
            task = new Task(taskItem[2], taskItem[4], taskStatus, id, localDateTime, duration);
        }
        super.tasks.put(task.getId(), task);
        if (maxId < id) {
            maxId = id;
        }
        return maxId;
    }

    private int epicFromString(String[] taskItem, int maxId) {
        Epic epic;
        int id = Integer.parseInt(taskItem[0]);
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Duration duration = Duration.ofMinutes(Long.parseLong(taskItem[6]));
        String startTime = taskItem[5];
        if (startTime.equals("Время начала не задано")) {
            epic = new Epic(taskItem[2], taskItem[4], taskStatus, id);
        } else {
            LocalDateTime dateStartTime = LocalDateTime.parse(taskItem[5]);
            LocalDateTime dateEndTime = LocalDateTime.parse(taskItem[7]);
            epic = new Epic(taskItem[2], taskItem[4], taskStatus, id, dateStartTime, duration, dateEndTime);
        }
        super.epics.put(epic.getId(), epic);
        if (maxId < id) {
            maxId = id;
        }
        return maxId;
    }

    private int subtaskFromString(String[] taskItem, int maxId) {
        Subtask subtask;
        String startTime = taskItem[6];
        int id = Integer.parseInt(taskItem[0]);
        int epicId = Integer.parseInt(taskItem[5]);
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Duration duration = Duration.ofMinutes(Long.parseLong(taskItem[7]));
        if (startTime.equals("Время начала не задано")) {
            subtask = new Subtask(taskItem[2], taskItem[4], taskStatus, id, epicId, duration);
        } else {
            LocalDateTime localDateTime = LocalDateTime.parse(taskItem[6]);
            subtask = new Subtask(taskItem[2], taskItem[4], taskStatus, id, epicId, localDateTime, duration);
        }
        super.subtasks.put(subtask.getId(), subtask);
        Epic epic = super.epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        if (maxId < id) {
            maxId = id;
        }
        return maxId;
    }
}
