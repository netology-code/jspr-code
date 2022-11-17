package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.*;

public class Server implements Runnable {

    protected ServerSocket serverSocket;
    protected List<String> validPaths;
    ExecutorService service;
    Thread t= new Thread(this);

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

}
