package com.reinert.client;

import com.reinert.common.HTTPBody;
import com.reinert.common.HTTPMethod;
import com.reinert.common.HTTPProtocol;
import com.reinert.common.HTTPUtil;

import java.io.IOException;
import java.net.URISyntaxException;

public class ChatClientTest {
    public static void main(String[] args) {
        try {
            // Make a client
            HTTPClient client = new HTTPClient(2626, HTTPUtil.makeURI("localhost"));
            HTTPProtocol p = HTTPProtocol.HTTP_1_1;
            // Execute a put request
            client.executeRequest(HTTPMethod.HEAD, HTTPUtil.makeURI("localhost/"), p, null);
            // Execute a put request
            client.executeRequest(HTTPMethod.PUT, HTTPUtil.makeURI("localhost/test.txt"), p, new HTTPBody("This is some data to put in a file."));
            // Execute a get request
            client.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI("localhost/test.txt"), p, null);
            // Execute a post request
            client.executeRequest(HTTPMethod.POST, HTTPUtil.makeURI("localhost/test.txt"), p, new HTTPBody("More data to put in a file!"));
            // Execute a put request
            client.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI("localhost/test.txt"), p, null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
