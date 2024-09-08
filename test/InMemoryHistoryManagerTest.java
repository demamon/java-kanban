import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import manager.HistoryManager;
import manager.ManagersUtils;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryHistoryManagerTest {
    private static HistoryManager hm;

    @BeforeAll
    static void beforeAll() {
        hm = ManagersUtils.getDefaultHistory();
    }


    @Test
    void add() {
        Task task = new Task("Test tasks.Task", "Test tasks.Task description", TaskStatus.NEW);

        hm.add(task);

        final List<Task> history = hm.getHistory();

        assertNotNull(history, "История пустая.");
    }

    @Test
    void getHistoryRepeatOneTask() {
        Task task = new Task("Test tasks.Task", "Test tasks.Task description", TaskStatus.NEW, 1);
        for (int i = 0; i < 6; i++) {
            hm.add(task);
        }
        final List<Task> history = hm.getHistory();
        System.out.println(history);
        assertEquals(1, history.size(), "В истории просмотренных задач задачи не заменяются.");
    }

    @Test
    void getHistoryFirstAndLastAndMediumTasks() {
        Task task = new Task("Test tasks.Task", "Test tasks.Task description", TaskStatus.NEW, 1);
        Task task2 = new Task("Test tasks2", "Test tasks2 description", TaskStatus.NEW, 2);
        Epic epic = new Epic("Test epic", "Test epic description", 3);
        Subtask subtask = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW, 4, epic.getId());
        hm.add(task2);
        hm.add(epic);
        hm.add(subtask);
        hm.add(task2);
        hm.add(task);
        final List<Task> history = hm.getHistory();
        assertEquals(task.getId(), history.getFirst().getId(), "Последний просмотренный элемент не верен.");
        assertEquals(epic.getId(), history.getLast().getId(), "Первый просмотренный элемент не верен.");
        assertEquals(subtask.getId(), history.get(2).getId(), "Средний просмотренный элемент не верен.");
    }

    @Test
    void deleteTaskHistory() {
        Task task = new Task("Test tasks.Task", "Test tasks.Task description", TaskStatus.NEW, 1);
        Task task2 = new Task("Test tasks2", "Test tasks2 description", TaskStatus.NEW, 2);
        Epic epic = new Epic("Test epic", "Test epic description", 3);
        Subtask subtask = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW, 4,
                epic.getId());
        Subtask subtask1 = new Subtask("Test subtask1", "Test subtask1 description", TaskStatus.NEW,
                5, epic.getId());
        hm.add(task2);
        hm.add(epic);
        hm.add(subtask1);
        hm.add(subtask);
        hm.add(task);
        hm.remove(task2.getId());
        List<Task> history = hm.getHistory();
        assertNotEquals(history.getLast(), task2, "Последний элемент удаляется не верно");
        hm.remove(task.getId());
        history = hm.getHistory();
        assertNotEquals(history.getFirst(), task, "Первый элемент удаляется не верно");
        hm.remove(subtask1.getId());
        history = hm.getHistory();
        assertFalse(history.contains(subtask1), "Средний элемент удаляется не верно");
        hm.remove(subtask.getId());
        hm.remove(epic.getId());
    }


}