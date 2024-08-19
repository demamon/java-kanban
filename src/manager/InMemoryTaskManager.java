package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int generationId = 0;
    final Map<Integer, Task> tasks = new HashMap<>();
    final Map<Integer, Epic> epics = new HashMap<>();
    final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager hm = ManagersUtils.getDefaultHistory();

    InMemoryTaskManager() {
    }

    @Override
    public int addNewTask(Task task) {
        final int id = ++generationId;
        task.setId(id);
        tasks.put(task.getId(), task);
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        final int id = ++generationId;
        epic.setId(id);
        epics.put(epic.getId(), epic);
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
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

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int id) {
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

    @Override
    public void clearTasks() {
        for (int id : tasks.keySet()) {
            hm.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (int id : epics.keySet()) {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (int idSubtask : subtaskIds) {
                subtasks.remove(idSubtask);
                hm.remove(idSubtask);
            }
            hm.remove(id);
        }
        epics.clear();

    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtaskId();
            updateEpicStatus(epic);
        }
        for (int id : subtasks.keySet()) {
            hm.remove(id);
        }
        subtasks.clear();

    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
            return null;
        }
        hm.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
            return null;
        }
        hm.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
            return null;
        }
        hm.add(subtasks.get(id));
        return subtasks.get(id);
    }


    @Override
    public void updateTask(Task task) {
        if (tasks.get(task.getId()) == null) {
            System.out.println("Задачи под номером " + task.getId() + " нет");
        } else {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
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

    @Override
    public void updateSubtask(Subtask subtask) {
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

    @Override
    public void deleteTask(int id) {
        if (tasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        } else {
            tasks.remove(id);
            hm.remove(id);
        }
    }

    @Override
    public void deleteEpic(int id) {
        if (epics.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        } else {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            for (int idSubtask : subtaskIds) {
                subtasks.remove(idSubtask);
                hm.remove(idSubtask);
            }
            epics.remove(id);
            hm.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        } else {
            Epic epic = epics.get(subtasks.get(id).getEpicId());
            epic.deleteSubtaskId(id);
            subtasks.remove(id);
            updateEpicStatus(epic);
            hm.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return hm.getHistory();
    }
}
