package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public Path path;

    FileBackedTaskManager(Path path) {
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
                fileWriter.write("id,type,name,status,description,epic" + "\n");
                for (Task task : super.tasks.values()) {
                    fileWriter.write(task.toString() + "\n");
                }
                for (Epic epic : super.epics.values()) {
                    fileWriter.write(epic.toString() + "\n");
                }
                for (Subtask subtask : super.subtasks.values()) {
                    fileWriter.write(subtask.toString() + "\n");
                }
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
        int id = Integer.parseInt(taskItem[0]);
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Task task = new Task(taskItem[2], taskItem[4], taskStatus, id);
        super.tasks.put(task.getId(), task);
        if (maxId < id) {
            maxId = id;
        }
        return maxId;
    }

    private int epicFromString(String[] taskItem, int maxId) {
        int id = Integer.parseInt(taskItem[0]);
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Epic epic = new Epic(taskItem[2], taskItem[4], taskStatus, id);
        super.epics.put(epic.getId(), epic);
        if (maxId < id) {
            maxId = id;
        }
        return maxId;
    }

    private int subtaskFromString(String[] taskItem, int maxId) {
        int id = Integer.parseInt(taskItem[0]);
        int epicId = Integer.parseInt(taskItem[5]);
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Subtask subtask = new Subtask(taskItem[2], taskItem[4], taskStatus, id, epicId);
        super.subtasks.put(subtask.getId(), subtask);
        Epic epic = super.epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
        if (maxId < id) {
            maxId = id;
        }
        return maxId;
    }

    public static void main(String[] args) {
        Path path = Paths.get("C:\\Users\\d-ba\\IdeaProjects\\java-kanban\\tasks-file.csv");
        FileBackedTaskManager tm = new FileBackedTaskManager(path);
        Task task = new Task("Test tasks", "Test tasks description", TaskStatus.NEW);
        Epic epic = new Epic("Test epic", "Test epic description");
        Epic epic2 = new Epic("Test epic2", "Test epic2 description");
        Subtask subtask = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW, 2);
        tm.addNewTask(task);
        tm.addNewEpic(epic);
        tm.addNewSubtask(subtask);
        tm.addNewEpic(epic2);
        System.out.println(tm.getTasks());
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubtasks());
        FileBackedTaskManager tm1 = loadFromFile(path);
        System.out.println(tm1.getTasks());
        System.out.println(tm1.getEpics());
        System.out.println(tm1.getSubtasks());
        System.out.println(tm1.getGenerationId());
    }
}
