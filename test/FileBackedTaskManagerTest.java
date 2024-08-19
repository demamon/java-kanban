import manager.FileBackedTaskManager;
import manager.ManagersUtils;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static TaskManager tm;
    Path path = Paths.get("C:\\Users\\d-ba\\IdeaProjects\\java-kanban\\tasks-file.csv");

    @BeforeAll
    static void beforeAll() {
        tm = ManagersUtils.getFileBacked();
    }

    @AfterEach
    void afterEach() {
        tm.clearEpics();
        tm.clearTasks();
    }

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
        int idTask = tm.addNewTask(task);
        int idEpic = tm.addNewEpic(epic);
        int idSubtask = tm.addNewSubtask(subtask);
        int idEpic2 = tm.addNewEpic(epic2);
        List<Task> tasksSave = tm.getTasks();
        List<Epic> epicsSave = tm.getEpics();
        List<Subtask> subtasksSave = tm.getSubtasks();
        FileBackedTaskManager tm1 = FileBackedTaskManager.loadFromFile(path);
        List<Task> tasksLoad = tm.getTasks();
        List<Epic> epicsLoad = tm.getEpics();
        List<Subtask> subtasksLoad = tm.getSubtasks();
        assertEquals(tasksSave, tasksLoad, "задачи не передаются в новый менеджер.");
        assertEquals(epicsSave, epicsLoad, "Эпики не передаются в новый менеджер.");
        assertEquals(subtasksSave, subtasksLoad, "Подзадачи не передаются в новый менеджер.");

    }
}
