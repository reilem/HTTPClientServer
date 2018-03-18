package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for a HTTP Pooled server. Contains a fixed size thread pool to handle clients.
 */
public class HTTPPooledServer implements Runnable {

    // Port that the server will be running on
    private final int port;
    // Boolean stores if server is running
    private boolean running = false;
    // ServerSocket of the server
    private ServerSocket serverSocket;
    // ExecutorService acts as a running clients thread pool.
    private ExecutorService runningClients = Executors.newFixedThreadPool(15);

    /**
     * Constructor for a pooled http server.
     * @param port  Port the server will be running on.
     */
    HTTPPooledServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        // Start the server
        startServer();
        // While server is running
        while (isRunning()) {
            try {
                // Accept client connection
                Socket client = this.serverSocket.accept();
                // Notify user
                System.out.println("\nA client has connected from host: " + client.getInetAddress().getCanonicalHostName());
                // Create a client handler for the client
                HTTPClientHandler handler = new HTTPClientHandler(client);
                // Execute the client handler
                this.runningClients.execute(handler);
            } catch (IOException e) {
                // If exception caught and server is not running. Notify user.
                if (!isRunning()) System.out.println("Server stopped.");
                else throw new RuntimeException("Error - failed to accept client: " + e.getMessage());
            }
        }
        // Shut down the thread pool.
        this.runningClients.shutdown();
    }

    /**
     * Stop the server.
     */
    synchronized void stopServer() {
        try {
            // Synchronize on this
            synchronized (this) {
                // Close the server socket
                this.serverSocket.close();
                // Set running to false
                this.running = false;
                // Notify all waiting for the server to finish shutting down
                this.notifyAll();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if server is running
     * @return  True if server is running.
     */
    synchronized boolean isRunning() {
        return this.running;
    }

    /**
     * Start the server.
     */
    private synchronized void startServer() {
        try {
            // Synchronize on this
            synchronized (this) {
                // Create the server socket on current port
                this.serverSocket = new ServerSocket(this.port);
                // Set running to true
                this.running = true;
                // Notify all waiting for the server to finish starting up
                this.notifyAll();
            }
            // Notify user
            System.out.println("Listening for client connections...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
