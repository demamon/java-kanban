import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import manager.HistoryManager;
import manager.ManagersUtils;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    void addMaxSize() {
        Task task = new Task("Test tasks.Task", "Test tasks.Task description", TaskStatus.NEW);

        for (int i = 0; i < 10; i++) {
            hm.add(task);
        }

        Task taskOverSize = new Task("Test Task1", "Test Task1 description", TaskStatus.IN_PROGRESS, 11);

        hm.add(taskOverSize);

        final List<Task> history = hm.getHistory();

        Task taskLastPosition = history.getLast();

        assertEquals(taskLastPosition, taskOverSize, "Добавление 11 задачи некорректно.");
    }
}