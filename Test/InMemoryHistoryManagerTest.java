import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static HistoryManager hm;

    @BeforeAll
    static void beforeAll() {
        hm = ManagersUtils.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Test Task", "Test Task description", TaskStatus.NEW);

        hm.add(task);

        final ArrayList<Task> history = hm.getHistory();

        assertNotNull(history, "История пустая.");
    }

    @Test
    void addMaxSize() {
        Task task = new Task("Test Task", "Test Task description", TaskStatus.NEW);

        for (int i = 0; i < 10; i++) {
            hm.add(task);
        }

        Task taskOverSize = new Task("Test Task1", "Test Task1 description", TaskStatus.IN_PROGRESS, 11);

        hm.add(taskOverSize);

        final ArrayList<Task> history = hm.getHistory();

        Task taskLastPosition = history.getLast();

        assertEquals(taskLastPosition, taskOverSize, "Добавление 11 задачи некорректно.");
    }
}