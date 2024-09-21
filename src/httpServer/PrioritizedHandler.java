package httpServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager tm;
    private final Gson gson;

    PrioritizedHandler(TaskManager tm, Gson gson) {
        this.tm = tm;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
            if (Objects.requireNonNull(endpoint) == Endpoint.GET_ALL) {
                handleGetPrioritized(exchange);
            } else {
                sendNotFound(exchange, "Такого пути нет");
            }
        } catch (IOException e) {
            internalServerError(exchange);
        }

    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        String prioritizedTasks = tm.getPrioritizedTasks().stream().map(gson::toJson).collect(Collectors.joining("\n"));
        sendText(exchange, prioritizedTasks);
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("prioritized") && requestMethod.equals("GET")) {
            return Endpoint.GET_ALL;
        }
        return Endpoint.UNKNOWN;
    }
}
