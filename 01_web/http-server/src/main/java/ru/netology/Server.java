package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

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
        boolean adding = false;
        if (!handlers.isEmpty()) {
            Iterator iterator = handlers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String,Map<String,Handler>> map = (Map.Entry)iterator.next();
                if (map.getKey().equals(method)) {
                    map.getValue().put(path,handler);
                    adding = true;
                    break;
                }
            }
        }
        if (!adding) {
            Map<String,Handler> map1 = new HashMap<>();
            map1.put(path,handler);
            handlers.put(method,map1);
        }
    }

}
