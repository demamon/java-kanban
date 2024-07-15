package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyViewedTasks = new ArrayList<>(10);

    InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        if (historyViewedTasks.size() == 10) {
            historyViewedTasks.removeFirst();
        }
            historyViewedTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        if (historyViewedTasks.isEmpty()) {
            System.out.println("Список просмотренных задач пуст");
        }
        return new ArrayList<>(historyViewedTasks);
    }
}
