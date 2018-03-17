package server;

public class HTTPServer {

    private final HTTPPooledServer server;

    public HTTPServer(int port) {
        this.server = new HTTPPooledServer(port);
    }

    public void start() {
        Thread serverThread = new Thread(this.server);
        serverThread.start();
    }

    public void stop() {
        server.stopServer();
    }

}
