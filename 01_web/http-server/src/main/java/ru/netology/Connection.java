package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class Connection implements Callable<Boolean> {

    private final Server server;

    protected Connection(Server server) {
        this.server = server;
    }

    @Override
    public Boolean call() throws Exception {
        try (
                final var socket = server.getServerSocket().accept();
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {

            final var limit = 4096;
            final var buffer = new char[limit];

            in.read(buffer,0,limit);

            Request request = new Request();
            setIsCorrect(buffer,limit,request);
            request.setRequestHeaders(setHeaders(buffer));

            if (!request.getIsCorrect()) {
                badRequest(out);
            } else {

                if (request.getIsQuery()) {
                    request.getQueryParams().stream().forEach(System.out::println);
                }

                if (!server.getHandlers().isEmpty()) {
                    var iterator = server.getHandlers().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Map<String, Handler>> pair = iterator.next();
                        if (request.getRequestMethod().equals(pair.getKey())) {
                            var iterator1 = pair.getValue().entrySet().iterator();
                            while (iterator1.hasNext()) {
                                Map.Entry<String, Handler> map1 = iterator1.next();
                                if (map1.getKey().equals(request.getPath())) {
                                    map1.getValue().handle(request, out);
                                    return false;
                                }
                            }
                        }
                    }
                }

                final var path = request.getPath();
                if (path == null) {
                    badRequest(out);
                    return false;
                } else {
                    if (!server.getValidPaths().contains(path)) {
                        badRequest(out);
                        return false;
                    }
                }

                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);

                // special case for classic
                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.write(content);
                    out.flush();
                }

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            }
            return false;
        }
    }

    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private static int indexOf(char[] array, char[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    public void setIsCorrect(char[] buffer, int limit, Request request) {
        char[] target = new char[]{'\r','\n'};
        int index = indexOf(buffer, target, 0 , limit);
        String[] firstLine = String.valueOf(buffer).substring(0, index).split(" ");
        if (firstLine.length != 3) {
            request.setIsCorrect(false);
        } else {
            request.setIsCorrect(true);
            request.setRequestMethod(firstLine[0]);
            if (firstLine[1].contains("?")) {
                request.setRequestPath(firstLine[1].substring(0, firstLine[1].indexOf("?")));
                request.setQueryLine(firstLine[1].substring(firstLine[1].indexOf("?"), firstLine[1].length() - 1));
                request.setIsQuery(true);
            } else {
                request.setRequestPath(firstLine[1]);
                request.setIsQuery(false);
            }
        }
    }

    protected List<NameValuePair> setHeaders(char[] buffer) {
        char[] targetF = new char[]{'\r', '\n', '\r', '\n'};
        char[] targetS = new char[]{'\r', '\n'};
        int startOfHeaders = indexOf(buffer,targetS,0,buffer.length)+2;
        int finishOfHeaders = indexOf(buffer,targetF,startOfHeaders,buffer.length);
        List<NameValuePair> requestHeaders = new ArrayList<>();
        String[] headersValues = String.valueOf(buffer).substring(startOfHeaders,finishOfHeaders).split("\r\n");
        Stream.of(headersValues)
                .forEach(x->{
                    String[] strgs = x.split(":");
                    requestHeaders.add(new BasicNameValuePair(strgs[0],strgs[1]));
                });
        return requestHeaders;
    }

}
