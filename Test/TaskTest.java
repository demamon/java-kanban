import org.junit.jupiter.api.Test;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void setIdTask() {
        Task task = new Task("task1", "task1", 1);
        task.setId(2);
        assertEquals(1, task.getId(), "id задачи изменился");
    }

    @Test
    void setNameTask() {
        Task task = new Task("task1", "task1", 1);
        task.setName("task2");
        assertEquals("task1", task.getName(), "имя задачи изменилось");
    }

    @Test
    void setDescriptionTask() {
        Task task = new Task("task1", "task1", 1);
        task.setDescription("task2");
        assertEquals("task1", task.getDescription(), "дескришин задачи изменился");
    }
}
