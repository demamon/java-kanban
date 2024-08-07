package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static class HistoryLinkedList {
        private HistoryListNode head;
        private HistoryListNode tail;
        private int size = 0;

        private HistoryListNode linkLast(Task task) {
            if (tail == null) {
                tail = new HistoryListNode(task);
                head = tail;
                size++;
                return tail;
            }
            HistoryListNode node = new HistoryListNode(task);
            tail.setNext(node);
            node.setPrev(tail);
            tail = node;
            size++;
            return tail;
        }

        private ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            HistoryListNode node = tail;
            while (node != null) {
                tasks.add(node.getData());
                node = node.getPrev();
            }
            return tasks;
        }

        private void removeNode(HistoryListNode node) {
            HistoryListNode prevNode = node.getPrev();
            HistoryListNode nextNode = node.getNext();
            if (nextNode == null && (prevNode != null)) {
                prevNode.setNext(null);
                tail = prevNode;
                size--;
                return;
            }
            if (nextNode == null) {
                tail = null;
                head = null;
                size--;
                return;
            }
            if (prevNode == null) {
                nextNode.setPrev(null);
                head = nextNode;
                size--;
                return;
            }
            prevNode.setNext(nextNode);
            nextNode.setPrev(prevNode);
            size--;
        }
    }

    private final HistoryLinkedList historyLinkedList = new HistoryLinkedList();
    private final Map<Integer, HistoryListNode> historyViewedTasks = new HashMap<>();

    InMemoryHistoryManager() {
    }

    @Override
    public void add(Task task) {
        if (historyViewedTasks.containsKey(task.getId())) {
            HistoryListNode node = historyViewedTasks.get(task.getId());
            historyLinkedList.removeNode(node);
        }
        historyViewedTasks.put(task.getId(), historyLinkedList.linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        if (historyViewedTasks.isEmpty()) {
            System.out.println("Список просмотренных задач пуст");
            return null;
        }
        return new ArrayList<>(historyLinkedList.getTasks());
    }

    @Override
    public void remove(int id) {
        if (!(historyViewedTasks.get(id) == null)) {
            HistoryListNode node = historyViewedTasks.get(id);
            historyLinkedList.removeNode(node);
            historyViewedTasks.remove(id);

        }
    }
}
