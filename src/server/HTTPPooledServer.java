package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HTTPPooledServer implements Runnable {

    private final int port;
    private boolean running = false;
    private ServerSocket serverSocket;
    private ExecutorService runningClients = Executors.newFixedThreadPool(15);

    HTTPPooledServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        startServer();
        System.out.println("Listening for client connections...");
        while (isRunning()) {
            try {
                Socket client = this.serverSocket.accept();
                System.out.println("\nA client has connected from host: " + client.getInetAddress().getCanonicalHostName());
                HTTPClientHandler handler = new HTTPClientHandler(client);
                this.runningClients.execute(handler);
            } catch (IOException e) {
                if (!isRunning()) System.out.println("Server stopped.");
                else throw new RuntimeException("Error - failed to accept client: " + e.getMessage());
            }
        }
        this.runningClients.shutdown();
    }

    synchronized void stopServer() {
        this.running = false;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean isRunning() {
        return this.running;
    }

    private synchronized void startServer() {
        this.running = true;
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
