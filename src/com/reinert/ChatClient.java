package com.reinert;

public class ChatClient {
    public static void main(String[] args) {
        // Fetch input parameters
        String method = args[0];
        String uri = args[1];
        int port = Integer.parseInt(args[2]);
        String protocol = args[3];

        // Make a client
        HTTPClient client = new HTTPClient(port, uri);
        // Execute its request
        System.out.println(client.executeRequest(method, uri, protocol, null));
    }
}
