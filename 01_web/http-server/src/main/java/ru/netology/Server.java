package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Server implements Runnable {

    protected ServerSocket serverSocket;
    protected List<String> validPaths;
    ExecutorService service;
    Map<String, Map<String,Handler>> handlers = new ConcurrentHashMap<>();
    Thread t = new Thread(this);

    public Server(int port, List<String> validPaths) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.validPaths = validPaths;
        service = Executors.newFixedThreadPool(64);
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Connection newConnection = new Connection(this);
                service.submit(newConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (handlers.containsKey(method)) {
            if (handlers.get(method).containsKey(path)) {
                handlers.get(method).put(path, handler);
            } else {
                Map<String,Handler> map = new HashMap<>();
                map.put(path,handler);
                handlers.put(method,map);
            }
        }
    }

}
