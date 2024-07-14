import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyViewedTasks = new ArrayList<>(10);

    InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        if (historyViewedTasks.size() == 10) {
            historyViewedTasks.removeFirst();
            historyViewedTasks.add(task);
        } else {
            historyViewedTasks.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        if (historyViewedTasks.isEmpty()) {
            System.out.println("Список просмотренных задач пуст");
        }
        return historyViewedTasks;
    }
}
