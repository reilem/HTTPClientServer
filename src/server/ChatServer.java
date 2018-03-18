package server;

public class ChatServer {
    public static void main(String[] args) {
        // Fetch port number from input arguments
        int port = Integer.parseInt(args[0]);
        // Create a HTTPServer
        HTTPServer server = new HTTPServer(port);
        try {
            // Start the server
            server.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
