import org.junit.jupiter.api.Test;
import tasks.Subtask;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    void setIdSubtask() {
        Subtask subtask = new Subtask("subtask1", "subtask1", TaskStatus.NEW, 1, 1);
        subtask.setId(2);
        assertEquals(1, subtask.getId(), "id подзадачи изменился");
    }

    @Test
    void setNameSubtask() {
        Subtask subtask = new Subtask("subtask1", "subtask1", TaskStatus.NEW, 1, 1);
        subtask.setName("task2");
        assertEquals("subtask1", subtask.getName(), "имя подзадачи изменилось");
    }

    @Test
    void setDescriptionSubtask() {
        Subtask subtask = new Subtask("subtask1", "subtask1", TaskStatus.NEW, 1, 1);
        subtask.setDescription("task2");
        assertEquals("subtask1", subtask.getDescription(), "дескришин подзадачи изменился");
    }
}