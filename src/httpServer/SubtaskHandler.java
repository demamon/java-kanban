package httpServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.IntersectionException;
import manager.NotFoundException;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager tm;
    private final Gson gson;

    SubtaskHandler(TaskManager tm, Gson gson) {
        this.tm = tm;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            switch (endpoint) {
                case GET_ALL: {
                    handleGetAllSubtask(exchange);
                    break;
                }
                case GET_ONE: {
                    handleGetSubTask(exchange);
                    break;
                }
                case POST: {
                    handlePostSubTask(exchange);
                    break;
                }
                case DELETE: {
                    handleDeleteSubTask(exchange);
                    break;
                }
                default:
                    sendNotFound(exchange, "Такого пути нет");
            }
        } catch (IOException e) {
            internalServerError(exchange);
        }
    }

    private void handleGetAllSubtask(HttpExchange exchange) throws IOException {
        String allSubtask = tm.getSubtasks().stream().map(gson::toJson).collect(Collectors.joining("\n"));
        sendText(exchange, allSubtask);
    }

    private void handleGetSubTask(HttpExchange exchange) throws IOException {
        try {
            int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            String subtask = gson.toJson(tm.getSubtask(id));
            sendText(exchange, subtask);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostSubTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtaskDeserialized = gson.fromJson(body, Subtask.class);
        int id = subtaskDeserialized.getId();
        try {
            if (id == 0) {
                tm.addNewSubtask(subtaskDeserialized);
                sendCode(exchange);
            } else {
                tm.updateSubtask(subtaskDeserialized);
                sendCode(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IntersectionException e) {
            sendIntersection(exchange, e.getMessage());
        }
    }

    private void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        try {
            int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            tm.deleteSubtask(id);
            sendText(exchange, "Задача успешно удалена!");
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("subtasks")) {
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

