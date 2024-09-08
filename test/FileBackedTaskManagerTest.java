import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import manager.TaskManager;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {

    private static final Path path = Paths.get("C:\\Users\\d-ba\\IdeaProjects\\java-kanban\\tasks-file.csv");
    private static final FileBackedTaskManager tm = new FileBackedTaskManager(path);

    @Test
    void saveAndLoadEmptyFile() {
        tm.clearTasks();
        System.out.println(tm.getTasks());
        FileBackedTaskManager tm1 = FileBackedTaskManager.loadFromFile(path);
        List<Task> tasks = tm1.getTasks();
        assertEquals(tasks, new ArrayList<>(), "Передача пустого файла в новый менджер не работает.");
    }

    @Test
    void saveAndLoadTasks() {
        Task task = new Task("Test tasks", "Test tasks description", TaskStatus.NEW);
        Epic epic = new Epic("Test epic", "Test epic description");
        Epic epic2 = new Epic("Test epic2", "Test epic2 description");
        Subtask subtask = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW, 2);
        tm.addNewTask(task);
        tm.addNewEpic(epic);
        tm.addNewSubtask(subtask);
        tm.addNewEpic(epic2);
        List<Task> tasksSave = tm.getTasks();
        List<Epic> epicsSave = tm.getEpics();
        List<Subtask> subtasksSave = tm.getSubtasks();
        FileBackedTaskManager tm1 = FileBackedTaskManager.loadFromFile(path);
        List<Task> tasksLoad = tm1.getTasks();
        List<Epic> epicsLoad = tm1.getEpics();
        List<Subtask> subtasksLoad = tm1.getSubtasks();
        assertEquals(tasksSave.toString(), tasksLoad.toString(), "задачи не передаются в новый менеджер.");
        assertEquals(epicsSave.toString(), epicsLoad.toString(), "Эпики не передаются в новый менеджер.");
        assertEquals(subtasksSave.toString(), subtasksLoad.toString(), "Подзадачи не передаются в новый менеджер.");
    }

    @Test
    void testException() {
        final Map<Integer, Task> tasks = new HashMap<>();
        Task task1 = new Task("Task1", "Task1", TaskStatus.NEW, 1);
        tasks.put(task1.getId(), task1);
        try {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(String.valueOf(path),
                    StandardCharsets.UTF_8))) {
                fileWriter.write("id,type,name,status,description,startTime,duration,endTime,epic" + "\n");
                tasks.values().forEach(task -> {
                    try {
                        fileWriter.write(task.toString() + "\n");
                    } catch (IOException e) {
                        assertThrows(IOException.class, fileWriter::close, "закрытие файла должно приводить к исключению");
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
}
