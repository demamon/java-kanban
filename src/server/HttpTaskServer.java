package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import manager.ManagersUtils;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private final TaskManager tm;
    private HttpServer httpServer;
    private final Gson gson;

    public HttpTaskServer(TaskManager tm) {
        this.tm = tm;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        TaskManager tm = ManagersUtils.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(tm);
        httpTaskServer.start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TaskHandler(tm, gson));
        httpServer.createContext("/subtask", new SubtaskHandler(tm, gson));
        httpServer.createContext("/epics", new EpicHandler(tm, gson));
        httpServer.createContext("/history", new HistoryHandler(tm, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(tm, gson));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    private static class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
        private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDateTime.format(dtf));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            JsonToken peek = jsonReader.peek();
            if (peek == null) {
                jsonReader.nextNull();
                return null;
            }
            return LocalDateTime.parse(jsonReader.nextString(), dtf);
        }
    }

    private static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            if (duration == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(duration.toString());
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            JsonToken peek = jsonReader.peek();
            if (peek == null) {
                jsonReader.nextNull();
                return null;
            }
            return Duration.parse(jsonReader.nextString());
        }
    }

    public Gson getGson() {
        return gson;
    }
}

