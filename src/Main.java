import manager.ManagersUtils;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

public static void main(String[] args) {
    TaskManager tm = ManagersUtils.getDefault();
    Task task = new Task("Test tasks", "Test tasks description", TaskStatus.NEW);
    Epic epic = new Epic("Test epic", "Test epic description");
    Epic epic2 = new Epic("Test epic2", "Test epic2 description");
    Subtask subtask = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW, 2);
    Subtask subtask2 = new Subtask("Test subtask2", "Test subtask2 description", TaskStatus.NEW, 2);
    Subtask subtask3 = new Subtask("Test subtask3", "Test subtask3 description", TaskStatus.NEW, 2);
    Task task2 = new Task("Test tasks2", "Test tasks2 description", TaskStatus.NEW);
    int idTask = tm.addNewTask(task);
    int idEpic = tm.addNewEpic(epic);
    int idSubtask = tm.addNewSubtask(subtask);
    int idSubtask2 = tm.addNewSubtask(subtask2);
    int idSubtask3 = tm.addNewSubtask(subtask3);
    int idEpic2 = tm.addNewEpic(epic2);
    int idTask2 = tm.addNewTask(task2);
    List<Task> history;
    tm.getTask(idTask);
    tm.getEpic(idEpic);
    tm.getSubtask(idSubtask);
    tm.getSubtask(idSubtask2);
    tm.getSubtask(idSubtask3);
    tm.getEpic(idEpic2);
    tm.getTask(idTask2);
    history = tm.getHistory();
    System.out.println("Запросили все элементы по одному разу - " + history);
    tm.getSubtask(idSubtask2);
    tm.getSubtask(idSubtask3);
    tm.getEpic(idEpic2);
    history = tm.getHistory();
    System.out.println("Запросили 3 повторных задачи - " + history);
    tm.deleteTask(idTask);
    history = tm.getHistory();
    System.out.println("Удалили 1 задачу - " + history);
    tm.deleteEpic(idEpic);
    history = tm.getHistory();
    System.out.println("Удалили эпик с 3 подзадачами - " + history);
}

