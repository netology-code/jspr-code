package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private ServerSocket serverSocket;
    private final List<String> validPaths;

    public Server(int port, List<String> validPaths) {
        this.port = port;
        this.validPaths = validPaths;
    }

    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ExecutorService service = Executors.newFixedThreadPool(64);
            this.serverSocket = serverSocket;
            while (true) {
                Connection newConnection = new Connection(this);
                service.submit(newConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public List<String> getValidPaths() {
        return validPaths;
    }

}
