package com.reinert.client;

import com.reinert.common.HTTP.HTTPMethod;
import com.reinert.common.HTTP.HTTPProtocol;
import com.reinert.common.HTTP.HTTPUtil;

import java.io.IOException;
import java.net.URISyntaxException;

public class ChatClient {
    public static void main(String[] args) {
        // Fetch input parameters
        String method = args[0];
        String uri = args[1];
        int port = Integer.parseInt(args[2]);
        String protocol = args[3];
        try {
            // Make a client
            HTTPClient client = new HTTPClient(port, HTTPUtil.makeURI(uri));
            // Execute its request
            client.executeRequest(HTTPMethod.parseMethod(method), HTTPUtil.makeURI(uri), HTTPProtocol.parseProtocol(protocol), null, null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
