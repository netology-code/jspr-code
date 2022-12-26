package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final List<String> VALID_PATHS = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final Map<String, Handler> map = new ConcurrentHashMap<>();
    public static final int PORT = 9999;
    private static final int THREADS_COUNT = 64;

    public void addHandler(String method, String path, Handler handler) {
        map.put(method + ":" + path, handler);
    }

    public void startServer() {
        final ExecutorService executorService = Executors.newFixedThreadPool(THREADS_COUNT);
        try (final var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try {
                    final Socket socket = serverSocket.accept();
                    executorService.submit(getServerTask(socket));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable getServerTask(Socket socket) {
        return () -> {
            try {
                handleConnection(socket);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private void handleConnection(Socket socket) throws IOException {
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())) {

            Request request = getRequest(in);
            if (request == null) {
                // just close socket
                return;
            }
            String requestPath = request.getPath();
            Handler handler = map.get(request.getMethod() + ":" + requestPath);

            if (handler == null) {
                if (!VALID_PATHS.contains(requestPath)) {
                    sendNotFoundResponse(out);
                } else {
                    sendResponse(out, requestPath);
                }
            } else {
                handler.handle(request, out);
            }

            out.flush();
        }
    }

    private Request getRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        List<String> parts = Arrays.asList(requestLine.split(" "));
        if (parts.size() != 3) {
            // just close socket
            return null;
        }

        StringBuilder headersStringBuilder = new StringBuilder();
        StringBuilder bodyStringBuilder = new StringBuilder();

        boolean hasBody = false;

        String inputLine = in.readLine();
        while (inputLine.length() > 0) {
            headersStringBuilder.append(inputLine);
            if (inputLine.startsWith("Content-Length: ")) {
                int index = inputLine.indexOf(':') + 1;
                String len = inputLine.substring(index).trim();
                hasBody = Integer.parseInt(len) > 0;
            }
            inputLine = in.readLine();
        }

        if (hasBody) {
            inputLine = in.readLine();
            while (inputLine != null && inputLine.length() > 0) {
                bodyStringBuilder.append(inputLine);
                inputLine = in.readLine();
            }
        }

        String fullPath = parts.get(1);
        String path = fullPath;
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        if (fullPath.contains("?")){
            path = fullPath.split("\\?")[0];
            nameValuePairs.addAll(parsQuery(fullPath));
        }

        return new Request(
                parts.get(0),
                path,
                fullPath,
                headersStringBuilder.toString(),
                bodyStringBuilder.toString(),
                nameValuePairs
        );
    }

    private List<NameValuePair> parsQuery(String path){
        try {
            return URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendNotFoundResponse(BufferedOutputStream out) throws IOException {
        Response Response = new Response(ResponseCode.NOT_FOUND, null, 0);
        writeResponse(Response, out);
    }

    private void writeResponse(Response data, BufferedOutputStream out) throws IOException {
        StringBuilder respBuilder = new StringBuilder();
        ResponseCode responseCode = data.getResponseCode();

        respBuilder.append("HTTP/1.1 ").append(responseCode.getCode()).append(" ").append(responseCode.getStatus()).append("\r\n");
        if (data.getContentType() != null) {
            respBuilder.append("Content-Type: ").append(data.getContentType()).append("\r\n");
        }
        respBuilder.append("Content-Length: ").append(data.getContentLength()).append("\r\n");
        respBuilder.append("Connection: close\r\n");
        respBuilder.append("\r\n");

        out.write(respBuilder.toString().getBytes());
    }

    private void sendResponse(BufferedOutputStream out, String path) throws IOException {
        Path filePath = Path.of("01_web/http-server", "public", path);
        String mimeType = Files.probeContentType(filePath);

        // special case for classic
        if (path.equals("/classic.html")) {
            String template = Files.readString(filePath);
            byte[] content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            Response Response = new Response(ResponseCode.OK, mimeType, content.length);
            writeResponse(Response, out);
            out.write(content);
        } else {
            Response Response = new Response(ResponseCode.OK, mimeType, Files.size(filePath));
            writeResponse(Response, out);
            Files.copy(filePath, out);
        }
    }
}