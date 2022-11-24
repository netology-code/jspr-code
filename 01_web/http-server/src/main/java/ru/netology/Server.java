package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Server {

    private ServerSocket serverSocket = null;
    private final List<String> validPaths;
    private final int port;
    private final Map<String, Map<String,Handler>> handlers = new ConcurrentHashMap<>();

    public Server(int port, List<String> validPaths) {
        this.port = port;
        this.validPaths = validPaths;
    }

    public void listen() {
        ExecutorService service = Executors.newFixedThreadPool(64);
        try(ServerSocket serverSocket = new ServerSocket(port)){
            this.serverSocket = serverSocket;
            while (true) {
                try {
                    Connection newConnection = new Connection(this);
                    service.submit(newConnection);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public List<String> getValidPaths() {
        return validPaths;
    }

    public int getPort() {
        return port;
    }

    public Map<String, Map<String, Handler>> getHandlers() {
        return handlers;
    }

}
