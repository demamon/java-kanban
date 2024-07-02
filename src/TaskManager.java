import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int generationId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final static HashMap<Integer, Subtask> subtasks = new HashMap<>();


    int addNewTask(Task task) {
        final int id = ++generationId;
        task.setId(id);
        tasks.put(task.getId(), task);
        return id;
    }

    int addNewEpic(Epic epic) {
        final int id = ++generationId;
        epic.setId(id);
        epics.put(epic.getId(), epic);
        return id;
    }

    int addNewSubtask(Subtask subtask) {
        Epic epic = getEpic(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Эпика под номером " + subtask.getEpicId() + " нет");
            return -1;
        }
        final int id = ++generationId;
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
        return id;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        TaskStatus status = null;
        for (int id : subtaskIds) {
            Subtask subtask = subtasks.get(id);
            if (status == null) {
                status = subtask.getStatus();
                continue;
            }
            if (status == subtask.getStatus() && status != TaskStatus.IN_PROGRESS) {
                continue;
            }
            status = TaskStatus.IN_PROGRESS;
            break;
        }
        if (status == TaskStatus.NEW) {
            epic.setStatus(TaskStatus.NEW);
        }
        if (status == TaskStatus.DONE) {
            epic.setStatus(TaskStatus.DONE);
        }
        if (status == TaskStatus.IN_PROGRESS) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    ArrayList<Subtask> getSubtasksForEpic(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            System.out.println("Задачи под номером " + id + " нет");
            return new ArrayList<>();
        }
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        for (int idSubtask : subtaskIds) {
            subtasksForEpic.add(subtasks.get(idSubtask));
        }
        return subtasksForEpic;
    }

    void clearTasks() {
        tasks.clear();
    }

    void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtaskId();
            updateEpicStatus(epic);
        }
    }

    Task getTask(int id) {
        if (tasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        }
        return tasks.get(id);
    }

    Epic getEpic(int id) {
        if (epics.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        }
        return epics.get(id);
    }

    Subtask getSubtask(int id) {
        if (subtasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        }
        return subtasks.get(id);
    }


    void updateTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            System.out.println("Задачи под номером " + task.getId() + " нет");
        } else {
            tasks.put(task.getId(), task);
        }
    }

    void updateEpic(Epic newEpic) {
        if (epics.get(newEpic.getId()) == null) {
            System.out.println("Задачи под номером " + newEpic.getId() + " нет");
        } else {
            Epic oldEpic = epics.get(newEpic.getId());
            ArrayList<Integer> subtaskIds = oldEpic.getSubtaskIds();
            epics.put(newEpic.getId(), newEpic);
            epics.get(newEpic.getId()).setSubtaskIds(subtaskIds);
            updateEpicStatus(epics.get(newEpic.getId()));
        }
    }

    void updateSubtask(Subtask subtask) {
        if (subtasks.get(subtask.getId()) == null) {
            System.out.println("Задачи под номером " + subtask.getId() + " нет");
        } else {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            if (oldSubtask.getEpicId() != subtask.getEpicId()) {
                System.out.println("В эпике " + subtask.getEpicId() + " такой задачи нет, " + "задача из эпика - " +
                        oldSubtask.getEpicId());
            } else {
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                updateEpicStatus(epic);
            }
        }
    }

    void deleteTask(int id) {
        if (tasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        } else {
            tasks.remove(id);
        }
    }

    void deleteEpic(int id) {
        if (epics.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        } else {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (int idSubtask : subtaskIds) {
                subtasks.remove(idSubtask);
            }
            epics.remove(id);
        }
    }

    void deleteSubtask(int id) {
        if (subtasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        } else {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.deleteSubtaskId(id);
            subtasks.remove(id);
            updateEpicStatus(epic);
        }
    }
}
