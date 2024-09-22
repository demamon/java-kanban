package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.IntersectionException;
import manager.NotFoundException;
import manager.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager tm;
    private final Gson gson;

    EpicHandler(TaskManager tm, Gson gson) {
        this.tm = tm;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            switch (endpoint) {
                case GET_ALL: {
                    handleGetAllEpic(exchange);
                    break;
                }
                case GET_ONE: {
                    handleGetEpic(exchange);
                    break;
                }
                case POST: {
                    handlePostEpic(exchange);
                    break;
                }
                case DELETE: {
                    handleDeleteEpic(exchange);
                    break;
                }
                case GET_SUBTASK_FOR_EPIC: {
                    handleGetEpicSubtasks(exchange);
                    break;
                }
                default:
                    sendNotFound(exchange, "Такого пути нет");
            }
        } catch (IOException e) {
            internalServerError(exchange);
        }
    }

    private void handleGetAllEpic(HttpExchange exchange) throws IOException {
        String allEpic = tm.getEpics().stream().map(gson::toJson).collect(Collectors.joining("\n"));
        sendText(exchange, allEpic, 200);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        try {
            int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            String epic = gson.toJson(tm.getEpic(id));
            sendText(exchange, epic, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epicDeserialized = gson.fromJson(body, Epic.class);
        int id = epicDeserialized.getId();
        try {
            if (id == 0) {
                String idNewTask = gson.toJson(tm.addNewEpic(epicDeserialized));
                sendText(exchange, idNewTask, 201);
            } else {
                tm.updateEpic(epicDeserialized);
                sendCode(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IntersectionException e) {
            sendIntersection(exchange, e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        try {
            int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            tm.deleteEpic(id);
            sendText(exchange, "Задача успешно удалена!", 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        try {
            int id = Integer.parseInt(exchange.getRequestURI().getPath().split("/")[2]);
            String epicSubtasks = tm.getSubtasksForEpic(id).stream().map(gson::toJson).collect(Collectors.joining("\n"));
            sendText(exchange, epicSubtasks, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ALL;
            }
            if (requestMethod.equals("POST")) {
                return Endpoint.POST;
            }
        }
        if (pathParts.length == 3 && pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_ONE;
            }
            if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE;
            }
        }
        if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks")) {
            return Endpoint.GET_SUBTASK_FOR_EPIC;
        }
        return Endpoint.UNKNOWN;
    }
}
