package server;

/**
 * Main class of a chat server.
 */
public class ChatServer {

    /**
     * The main function of the chat server. Creates a server and starts it based on the given parameters.
     * @param args The String array of given parameters. Must contain a valid port for your current setup.
     */
    public static void main(String[] args) {
        // Fetch port number from input arguments
        int port = Integer.parseInt(args[0]);
        // Create a HTTPServer
        HTTPServer server = new HTTPServer(port);
        try {
            // Start the server
            server.start();
        } catch (InterruptedException e) {
            // Catch any interrupted thread exceptions.
            System.err.println("Internal error occurred while waiting for server to startup. Please try again.");
            e.printStackTrace();
        }
    }
}
