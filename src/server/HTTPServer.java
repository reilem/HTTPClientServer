package server;

/**
 * Class for a HTTP server. Places a HTTPPooledServer in a thread and starts the thread.
 */
public class HTTPServer {

    // Pooled HTTP server that uses a pool of threads.
    private final HTTPPooledServer server;

    /**
     * Constructor for a HTTP Server.
     * @param port  Given port to start the server on.
     */
    public HTTPServer(int port) {
        this.server = new HTTPPooledServer(port);
    }

    /**
     * Starts the server.
     * @throws InterruptedException If something goes wrong when waiting for server to start up.
     */
    public void start() throws InterruptedException {
        // Place the server in a thread
        Thread serverThread = new Thread(this.server);
        // Start the server thread
        serverThread.start();
        // Synchronize with the server
        synchronized (this.server) {
            // Wait for server to start up before returning
            while (!this.server.isRunning()) {
                this.server.wait();
            }
        }
    }

    /**
     * Stops the server.
     * @throws InterruptedException If something goes wrong when waiting for server to shut down.
     */
    public void stop() throws InterruptedException {
        // Synchronize with the server
        synchronized (this.server) {
            // Stop the server
            this.server.stopServer();
            // Wait for it to stop running before returning
            while (this.server.isRunning()) {
                this.server.wait();
            }
        }

    }

}
