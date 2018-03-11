package com.reinert;

public class ChatClientTest {
    public static void main(String[] args) {
        // Make a client
        HTTPClient client = new HTTPClient(2626, "localhost");
        // Execute a put request
        System.out.println(client.executeRequest("HEAD", "localhost/", "HTTP/1.1", null, null));
        // Execute a put request
        System.out.println(client.executeRequest("PUT", "localhost/test.txt", "HTTP/1.1", null, "This is some data to put in a file.\n"));
        // Execute a get request
        System.out.println(client.executeRequest("GET", "localhost/test.txt", "HTTP/1.1", null, null));
        // Execute a post request
        System.out.println(client.executeRequest("POST", "localhost/test.txt", "HTTP/1.1", null, "More data to put in a file!"));
        // Execute a put request
        System.out.println(client.executeRequest("GET", "localhost/test.txt", "HTTP/1.1", null, null));
    }
}
