import org.junit.jupiter.api.Test;
import tasks.Epic;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    void setIdEpic() {
        Epic epic = new Epic("epic1", "epic1", 1);
        epic.setId(2);
        assertEquals(1, epic.getId(), "id эпика изменился");
    }

    @Test
    void setNameEpic() {
        Epic epic = new Epic("epic1", "epic1", 1);
        epic.setName("task2");
        assertEquals("epic1", epic.getName(), "имя эпика изменилось");
    }

    @Test
    void setDescriptionEpic() {
        Epic epic = new Epic("epic1", "epic1", 1);
        epic.setDescription("task2");
        assertEquals("epic1", epic.getDescription(), "дескришин эпика изменился");
    }
}