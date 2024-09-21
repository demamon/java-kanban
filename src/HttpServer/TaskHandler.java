package HttpServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.IntersectionException;
import manager.NotFoundException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager tm;
    private final Gson gson;

    TaskHandler(TaskManager tm, Gson gson) {
        this.tm = tm;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            switch (endpoint) {
                case GET_ALL: {
                    handleGetAllTasks(exchange);
                    break;
                }
                case GET_ONE: {
                    handleGetTask(exchange);
                    break;
                }
                case POST: {
                    handlePostTask(exchange);
                    break;
                }
                case DELETE: {
                    handleDeleteTask(exchange);
                    break;
                }
                default:
                    sendNotFound(exchange, "Такого пути нет");
            }
        } catch (IOException e) {
            internalServerError(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String allTasks = tm.getTasks().stream().map(gson::toJson).collect(Collectors.joining("\n"));
        sendText(exchange, allTasks);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        try {
            int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            String task = gson.toJson(tm.getTask(id));
            sendText(exchange, task);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task taskDeserialized = gson.fromJson(body, Task.class);
        int id = taskDeserialized.getId();
        try {
            if (id == 0) {
                tm.addNewTask(taskDeserialized);
                sendCode(exchange);
            } else {
                tm.updateTask(taskDeserialized);
                sendCode(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IntersectionException e) {
            sendIntersection(exchange, e.getMessage());
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        try {
            int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            tm.deleteTask(id);
            sendText(exchange, "Задача успешно удалена!");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ONE;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE;
            }
        }
        return Endpoint.UNKNOWN;
    }
}
