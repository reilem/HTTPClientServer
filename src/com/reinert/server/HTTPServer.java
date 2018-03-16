package com.reinert.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HTTPServer {

    private final int port;

    public HTTPServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Server is running.");

        try {
            ServerSocket httpServerSocket = new ServerSocket(port);

            System.out.println("Listening for client connections...");

            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < 120000) {
                Socket client = httpServerSocket.accept();
                System.out.println("\nA client has connected from host: " + client.getInetAddress().getCanonicalHostName());
                HTTPClientHandler handler = new HTTPClientHandler(client);
                Thread clientThread = new Thread(handler);
                clientThread.start();
            }
            System.out.println("Server timed out.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
