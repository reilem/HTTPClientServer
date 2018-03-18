package server;

public class HTTPServer {

    private final HTTPPooledServer server;

    public HTTPServer(int port) {
        this.server = new HTTPPooledServer(port);
    }

    public void start() throws InterruptedException {
        Thread serverThread = new Thread(this.server);
        serverThread.start();
        synchronized (this.server) {
            // Wait for server to start up
            while (!this.server.isRunning()) {
                this.server.wait();
            }
        }
    }

    public void stop() {
        server.stopServer();
    }

}
