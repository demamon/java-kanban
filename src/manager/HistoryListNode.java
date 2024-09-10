package manager;

import tasks.Task;

import java.util.Objects;

public class HistoryListNode {
    private Task data;
    private HistoryListNode next;
    private HistoryListNode prev;

    public HistoryListNode(Task data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }

    public Task getData() {
        return data;
    }

    public void setData(Task data) {
        this.data = data;
    }

    public HistoryListNode getNext() {
        return next;
    }

    public void setNext(HistoryListNode next) {
        this.next = next;
    }

    public HistoryListNode getPrev() {
        return prev;
    }

    public void setPrev(HistoryListNode prev) {
        this.prev = prev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistoryListNode node)) return false;
        return Objects.equals(data, node.data) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, next, prev);
    }

    @Override
    public String toString() {
        return "HistoryListNode{" +
                "data=" + data +
                ", next=" + next +
                ", prev=" + prev +
                '}';
    }
}
