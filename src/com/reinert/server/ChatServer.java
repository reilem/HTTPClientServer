package com.reinert.server;

public class ChatServer {
    public static void main(String[] args) {
        // Fetch port number from input arguments
        int port = Integer.parseInt(args[0]);
        // Create a HTTPServer
        HTTPServer server = new HTTPServer(port);
        // Start the server
        server.start();
    }
}
