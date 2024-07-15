import manager.ManagersUtils;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static TaskManager tm;

    @BeforeAll
    static void beforeAll() {
        tm = ManagersUtils.getDefault();
    }

    @AfterEach
    void afterEach() {
        tm.clearEpics();
        tm.clearTasks();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = tm.addNewTask(task);
        final Task savedTask = tm.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = tm.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = tm.addNewEpic(epic);
        final Epic savedEpic = tm.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = tm.getEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = tm.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = tm.addNewSubtask(subtask);
        final Subtask savedSubtask = tm.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = tm.getSubtasks();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test oldTask", "Test oldTask description", TaskStatus.NEW);
        final int taskId = tm.addNewTask(task);
        final Task updateTask = new Task("Test updateTask", "Test updateTask description",
                TaskStatus.IN_PROGRESS, taskId);

        tm.updateTask(updateTask);

        final Task savedUpdateTask = tm.getTask(taskId);

        assertEquals(updateTask, savedUpdateTask, "Задача не обновилась.");

        Task taskNonExistentUpdate = new Task("Test updateTask", "Test updateTask description",
                TaskStatus.IN_PROGRESS, 10);

        tm.updateTask(taskNonExistentUpdate);
        taskNonExistentUpdate = tm.getTask(10);
        assertNull(taskNonExistentUpdate, "Несуществующая задача обновилась.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test oldEpic", "Test oldEpic description");
        final int epicId = tm.addNewEpic(epic);
        Subtask subtask = new Subtask("Test subtask", "Test subtask description",
                TaskStatus.NEW, epicId);

        tm.addNewSubtask(subtask);

        List<Subtask> subtasksForOldEpic = tm.getSubtasksForEpic(epicId);
        Epic updateEpic = new Epic("Test updateEpic", "Test updateEpic description", epicId);

        tm.updateEpic(updateEpic);

        List<Subtask> subtasksForUpdateEpic = tm.getSubtasksForEpic(epicId);
        final Epic savedUpdateEpic = tm.getEpic(epicId);

        assertEquals(updateEpic, savedUpdateEpic, "Эпик не обновился.");
        assertEquals(subtasksForOldEpic, subtasksForUpdateEpic, "Эпик обновился, но подзадачи не совпадают.");

        Epic epicNonExistentUpdate = new Epic("Test oldEpic", "Test oldEpic description", 10);

        tm.updateEpic(epicNonExistentUpdate);
        epicNonExistentUpdate = tm.getEpic(10);
        assertNull(epicNonExistentUpdate, "Несуществующий эпик обновился.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Test epic", "Test epic description");
        final int epicID = tm.addNewEpic(epic);
        Subtask oldSubtask = new Subtask("Test oldSubtasak", "Test oldSubtask", TaskStatus.NEW, epicID);
        final int oldSubtaskId = tm.addNewSubtask(oldSubtask);
        Subtask updateSubtask = new Subtask("Test oldSubtasak", "Test oldSubtask", TaskStatus.NEW,
                oldSubtaskId, epicID);

        tm.updateSubtask(updateSubtask);

        final Subtask savedUpdateSubtask = tm.getSubtask(oldSubtaskId);

        assertEquals(updateSubtask, savedUpdateSubtask, "Подзадача не обновилась.");

        Epic epicNoSubtasks = new Epic("Test epicNoSubtasks", "Test epicNoSubtasks description");
        final int epicNoSubtasksID = tm.addNewEpic(epicNoSubtasks);
        Subtask subtaskIncorrectEpicId = new Subtask("Test subtaskIncorrectEpicId", "subtaskIncorrectEpicId",
                TaskStatus.NEW, oldSubtaskId, epicNoSubtasksID);

        tm.updateSubtask(subtaskIncorrectEpicId);

        List<Subtask> subtasksForEpicNoSubtasksID = tm.getSubtasksForEpic(epicNoSubtasksID);

        assertEquals(subtasksForEpicNoSubtasksID, new ArrayList<>(), "Подзадача добавлена не в свой эпик.");

        Subtask subtaskNonExistentUpdate = new Subtask("Test oldSubtasak", "Test oldSubtask",
                TaskStatus.NEW, 10, epicID);

        tm.updateSubtask(subtaskNonExistentUpdate);
        subtaskNonExistentUpdate = tm.getSubtask(10);
        assertNull(subtaskNonExistentUpdate, "Несуществующая подзадача обновилась.");
    }

    @Test
    void deleteTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", TaskStatus.NEW);
        final int taskId = tm.addNewTask(task);

        tm.deleteTask(taskId);
        task = tm.getTask(taskId);
        assertNull(task, "Задача не удалена.");
    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = tm.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                TaskStatus.NEW, epicId);
        final int subtaskId = tm.addNewSubtask(subtask);

        tm.deleteEpic(epicId);
        epic = tm.getEpic(epicId);
        assertNull(epic, "Эпик не удален.");
        subtask = tm.getSubtask(subtaskId);
        assertNull(subtask, "Эпик удален, а подзадача нет");
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        final int epicId = tm.addNewEpic(epic);

        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                TaskStatus.NEW, epicId);

        final int subtaskId = tm.addNewSubtask(subtask);

        tm.deleteSubtask(subtaskId);
        subtask = tm.getSubtask(subtaskId);
        assertNull(subtask, "Подзадача не удалена.");
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        assertFalse(subtaskIds.contains(subtaskId), "Подзадача не удалена из эпика.");

    }

    @Test
    void getHistoryWithUpdateTask() {
        Task task = new Task("Test oldTask", "Test oldTask description", TaskStatus.NEW);
        final int taskId = tm.addNewTask(task);

        tm.getTask(taskId);

        final List<Task> historyTask = tm.getHistory();
        Task UptadeTask = new Task("Test UpdateTask", "Test UpdateTask description", TaskStatus.DONE, taskId);

        tm.updateTask(UptadeTask);

        Task oldTask = historyTask.getLast();

        assertEquals(oldTask, task, "История не сохраняет предыдущие данные задачи.");
    }

    @Test
    void updateEpicStatus() {
        Epic epic = new Epic("Test tasks.Epic", "Test tasks.Epic description");
        final int epicId = tm.addNewEpic(epic);

        epic = tm.getEpic(epicId);
        assertEquals(epic.getStatus(), TaskStatus.NEW, "Пустой эпик не имеет статус NEW.");

        Subtask subtaskNew1 = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW, epicId);
        Subtask subtaskNew2 = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW, epicId);

        final int newSubtask1Id = tm.addNewSubtask(subtaskNew1);
        final int newSubtask2Id = tm.addNewSubtask(subtaskNew2);

        epic = tm.getEpic(epicId);
        assertEquals(epic.getStatus(), TaskStatus.NEW, "Эпик с подзадачами в статусе NEW не имеет статуса NEW.");

        Subtask subtaskDone = new Subtask("Test subtask", "Test subtask description", TaskStatus.DONE, epicId);

        tm.addNewSubtask(subtaskDone);
        epic = tm.getEpic(epicId);
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Эпик с подзадачами c разными статусами не имеет" +
                " статуса IN_PROGRESS.");
        tm.deleteSubtask(newSubtask1Id);
        tm.deleteSubtask(newSubtask2Id);
        epic = tm.getEpic(epicId);
        assertEquals(epic.getStatus(), TaskStatus.DONE, "Эпик с подзадачами в статусе DONE не имеет статуса DONE.");
    }

}