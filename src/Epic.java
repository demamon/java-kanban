import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.status = TaskStatus.NEW;
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.status = TaskStatus.NEW;
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void deleteSubtaskId(Integer idSubtask) {
        subtaskIds.remove(idSubtask);
    }

    public void clearSubtaskId() {
        subtaskIds.clear();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }

}
