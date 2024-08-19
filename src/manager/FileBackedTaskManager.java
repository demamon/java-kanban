package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    static public Path path = null;

    FileBackedTaskManager() {
        path = Paths.get("C:\\Users\\d-ba\\IdeaProjects\\java-kanban\\tasks-file.csv");
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
    public List<Task> getTasks() {
        return super.getTasks();
    }

    @Override
    public List<Epic> getEpics() {
        return super.getEpics();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return super.getSubtasks();
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int id) {
        return super.getSubtasksForEpic(id);
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
    public Task getTask(int id) {
        return super.getTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        return super.getEpic(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return super.getSubtask(id);
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

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    private void save() {
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
            System.out.println("Произошла ошибка во время записи файла.");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager tm = new FileBackedTaskManager();
        try (BufferedReader fileReader = new BufferedReader(new FileReader(String.valueOf(path),
                StandardCharsets.UTF_8))) {
            while (fileReader.ready()) {
                String task = fileReader.readLine();
                String[] taskItem = task.split(",");
                if (taskItem[1].equals(TaskType.TASK.toString())) {
                    tm.taskFromString(taskItem);
                } else if (taskItem[1].equals(TaskType.EPIC.toString())) {
                    tm.epicFromString(taskItem);
                } else if (taskItem[1].equals(TaskType.SUBTASK.toString())) {
                    tm.subtaskFromString(taskItem);
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
        return tm;
    }

    private void taskFromString(String[] taskItem) {
        int id = Integer.parseInt(taskItem[0]);
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Task task = new Task(taskItem[2], taskItem[4], taskStatus, id);
        super.tasks.put(task.getId(), task);
    }

    private void epicFromString(String[] taskItem) {
        int id = Integer.parseInt(taskItem[0]);
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Epic epic = new Epic(taskItem[2], taskItem[4], taskStatus, id);
        super.epics.put(epic.getId(), epic);
    }

    private void subtaskFromString(String[] taskItem) {
        int id = Integer.parseInt(taskItem[0]);
        int epicId = Integer.parseInt(taskItem[5]);
        TaskStatus taskStatus = TaskStatus.valueOf(taskItem[3]);
        Subtask subtask = new Subtask(taskItem[2], taskItem[4], taskStatus, id, epicId);
        super.subtasks.put(subtask.getId(), subtask);
        Epic epic = super.epics.get(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
    }

    public static void main(String[] args) {
        TaskManager tm = ManagersUtils.getFileBacked();
        Task task = new Task("Test tasks", "Test tasks description", TaskStatus.NEW);
        Epic epic = new Epic("Test epic", "Test epic description");
        Epic epic2 = new Epic("Test epic2", "Test epic2 description");
        Subtask subtask = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW, 2);
        int idTask = tm.addNewTask(task);
        int idEpic = tm.addNewEpic(epic);
        int idSubtask = tm.addNewSubtask(subtask);
        int idEpic2 = tm.addNewEpic(epic2);
        System.out.println(tm.getTasks());
        System.out.println(tm.getEpics());
        System.out.println(tm.getSubtasks());
        FileBackedTaskManager tm1 = loadFromFile(path);
        System.out.println(tm1.getTasks());
        System.out.println(tm1.getEpics());
        System.out.println(tm1.getSubtasks());
    }
}
