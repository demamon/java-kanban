import java.util.ArrayList;

public class Epic extends Task{

    protected ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(String name, String description, TaskStatus status, int id) {
        super(name, description, status, id);
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void deleteSubtaskId(Integer idSubtask) {
        subtaskIds.remove(idSubtask);
    }

    public void clearSubtaskId (){
       subtaskIds.clear();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
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
