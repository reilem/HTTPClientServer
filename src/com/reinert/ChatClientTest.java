package com.reinert;

public class ChatClientTest {
    public static void main(String[] args) {
        // Make a client
        HTTPClient client = new HTTPClient(2626, "localhost");
        // Execute a put request
        client.executeRequest("HEAD", "localhost/", "HTTP/1.1", null);
        // Execute a put request
        client.executeRequest("PUT", "localhost/test.txt", "HTTP/1.1", "This is some data to put in a file."+HTTPUtil.CRLF);
        // Execute a get request
        client.executeRequest("GET", "localhost/test.txt", "HTTP/1.1", null);
        // Execute a post request
        client.executeRequest("POST", "localhost/test.txt", "HTTP/1.1", "More data to put in a file!"+HTTPUtil.CRLF);
        // Execute a put request
        client.executeRequest("GET", "localhost/test.txt", "HTTP/1.1", null);
    }
}
