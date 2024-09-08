package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int generationId = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTask = new TreeSet<>();
    private final HistoryManager hm = ManagersUtils.getDefaultHistory();

    InMemoryTaskManager() {
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTask;
    }

    @Override
    public int addNewTask(Task task) {
        if (task.getStartTime() != null) {
            if (isIntersection(task)) {
                System.out.println("Данная задача пересекается по времени с другой задачей.");
                return -1;
            }
            prioritizedTask.add(task);
        }
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
        if (subtask.getStartTime() != null) {
            if (isIntersection(subtask)) {
                System.out.println("Данная задача пересекается по времени с другой задачей.");
                return -1;
            }
            prioritizedTask.add(subtask);
        }
        final int id = ++generationId;
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);
        updateEpicDurationAndStartTime(epic);
        return id;
    }

    private void updateEpicDurationAndStartTime(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        Optional<LocalDateTime> startTime = subtaskIds.stream()
                .map(subtaskId -> Optional.ofNullable(subtasks.get(subtaskId)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(subtask -> Optional.ofNullable(subtask.getStartTime()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .min(Comparator.comparing(localDateTime1 -> localDateTime1));
        startTime.ifPresent(epic::setStartTime);
        Optional<Duration> duration = subtaskIds.stream()
                .map(subtaskId -> Optional.ofNullable(subtasks.get(subtaskId)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(subtask -> Optional.ofNullable(subtask.getDuration()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .reduce(Duration::plus);
        duration.ifPresent(epic::setDuration);
        Optional<LocalDateTime> endTime = subtaskIds.stream()
                .map(subtaskId -> Optional.ofNullable(subtasks.get(subtaskId)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(subtask -> {
                    if (subtask.getStartTime() == null) {
                        return null;
                    }
                    return subtask.getStartTime().plus(subtask.getDuration());
                })
                .filter(Objects::nonNull)
                .max(Comparator.comparing(localDateTime1 -> localDateTime1));
        endTime.ifPresent(epic::setEndTime);
    }

    private boolean isIntersection(Task task) {
        boolean isIntersectionStartTime = prioritizedTask.stream()
                .map(Task::getStartTime)
                .anyMatch(setStartTime -> setStartTime.equals(task.getEndTime()) ||
                        setStartTime.isBefore(task.getEndTime()));
        boolean isIntersectionEndTime = prioritizedTask.stream()
                .map(Task::getEndTime)
                .anyMatch(setEndTime -> setEndTime.equals(task.getStartTime()) || setEndTime.isAfter(task.getStartTime()));
        return isIntersectionStartTime && isIntersectionEndTime;
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
        boolean isNew = subtaskIds.stream()
                .map(id -> subtasks.get(id).getStatus())
                .allMatch(taskStatus -> taskStatus == TaskStatus.NEW);
        boolean isDone = subtaskIds.stream()
                .map(id -> subtasks.get(id).getStatus())
                .allMatch(taskStatus -> taskStatus == TaskStatus.DONE);

        if (isNew) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        if (isDone) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }
        epic.setStatus(TaskStatus.IN_PROGRESS);
    }

    private void deleteOldTaskForSet(Task task) {
        Optional<Integer> mayBeIdTask = prioritizedTask.stream()
                .map(Task::getId)
                .filter(idSet -> idSet == task.getId()).findFirst();
        if (mayBeIdTask.isPresent()) {
            Task oldTask = tasks.get(mayBeIdTask.get());
            prioritizedTask.remove(oldTask);
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
        return subtaskIds.stream()
                .map(subtasks::get)
                .collect(Collectors.toList());

    }

    @Override
    public void clearTasks() {
        tasks.keySet().forEach(id -> {
            hm.remove(id);
            if (!prioritizedTask.isEmpty()) {
                prioritizedTask.remove(tasks.get(id));
            }
        });
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.keySet().forEach(id -> {
            Epic epic = epics.get(id);
            ArrayList<Integer> subtaskIds = epic.getSubtaskIds();
            subtaskIds.forEach(subtaskId -> {
                subtasks.remove(subtaskId);
                hm.remove(subtaskId);
                if (!prioritizedTask.isEmpty()) {
                    prioritizedTask.remove(subtasks.get(subtaskId));
                }
            });
            hm.remove(id);
        });
        epics.clear();

    }

    @Override
    public void clearSubtasks() {
        epics.values().forEach(epic -> {
            epic.clearSubtaskId();
            updateEpicStatus(epic);
            updateEpicDurationAndStartTime(epic);
        });
        subtasks.keySet().forEach(id -> {
            hm.remove(id);
            Subtask subtask = subtasks.get(id);
            if (!prioritizedTask.isEmpty()) {
                prioritizedTask.remove(subtask);
            }
        });
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
            return;
        }
        if (task.getStartTime() != null) {
            if (isIntersection(task)) {
                System.out.println("Данная задача пересекается по времени с другой задачей.");
                return;
            }
            deleteOldTaskForSet(task);
            prioritizedTask.add(task);
        }
        deleteOldTaskForSet(task);
        tasks.put(task.getId(), task);
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
            updateEpicDurationAndStartTime(epics.get(newEpic.getId()));
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.get(subtask.getId()) == null) {
            System.out.println("Задачи под номером " + subtask.getId() + " нет");
            return;
        }
        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask.getEpicId() != subtask.getEpicId()) {
            System.out.println("В эпике " + subtask.getEpicId() + " такой задачи нет, " + "задача из эпика - " +
                    oldSubtask.getEpicId());
            return;
        }
        if (subtask.getStartTime() != null) {
            if (isIntersection(subtask)) {
                System.out.println("Данная задача пересекается по времени с другой задачей.");
                return;
            }
            deleteOldTaskForSet(subtask);
            prioritizedTask.add(subtask);
        }
        deleteOldTaskForSet(subtask);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
        updateEpicDurationAndStartTime(epic);
    }

    @Override
    public void deleteTask(int id) {
        if (tasks.get(id) == null) {
            System.out.println("Задачи под номером " + id + " нет");
        } else {
            Task task = tasks.get(id);
            if (!prioritizedTask.isEmpty()) {
                prioritizedTask.remove(task);
            }
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
            subtaskIds.forEach(idSubtask -> {
                if (!prioritizedTask.isEmpty()) {
                    prioritizedTask.remove(subtasks.get(idSubtask));
                }
                subtasks.remove(idSubtask);
                hm.remove(idSubtask);
            });
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
            if (!prioritizedTask.isEmpty()) {
                prioritizedTask.remove(subtasks.get(id));
            }
            subtasks.remove(id);
            updateEpicStatus(epic);
            updateEpicDurationAndStartTime(epic);
            hm.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return hm.getHistory();
    }

    public int getGenerationId() {
        return generationId;
    }

    public void setGenerationId(int generationId) {
        this.generationId = generationId;
    }
}
