import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersUtilsTest {

    @Test
    void getDefault() {
        TaskManager tm = ManagersUtils.getDefault();
        assertNotNull(tm, "Вызов менеджера задач не удался");
    }

    @Test
    void getDefaultHistory() {
        HistoryManager hm = ManagersUtils.getDefaultHistory();
        assertNotNull(hm, "Вызов менеджера истории не удался");
    }
}