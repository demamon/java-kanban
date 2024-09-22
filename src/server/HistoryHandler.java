package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.NotFoundException;
import manager.TaskManager;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager tm;
    private final Gson gson;

    HistoryHandler(TaskManager tm, Gson gson) {
        this.tm = tm;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            if (Objects.requireNonNull(endpoint) == Endpoint.GET_ALL) {
                handleGetHistory(exchange);
            } else {
                sendNotFound(exchange, "Такого пути нет");
            }
        } catch (IOException e) {
            internalServerError(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            String historyViewing = tm.getHistory().stream().map(gson::toJson).collect(Collectors.joining("\n"));
            sendText(exchange, historyViewing, 200);
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_ALL;
        }
        return Endpoint.UNKNOWN;
    }
}
