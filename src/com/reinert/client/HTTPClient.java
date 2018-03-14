package com.reinert.client;

import com.reinert.common.HTTPRequest;
import com.reinert.common.HTTPResponse;
import com.reinert.common.HTTPUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;

public class HTTPClient {

    private final Socket httpSocket;
    private final String host;

    HTTPClient(int port, String uriString) throws IOException, URISyntaxException {
        // Create socket based on host name provided by uri
        this.host = HTTPUtil.parseHostName(uriString);
        this.httpSocket = new Socket(InetAddress.getByName(this.host), port);
    }

    /**
     * Executes a HTTP request with given parameters.
     * @param method        the HTTP method of the request
     * @param requestURI    the uri of the request
     * @param protocol      the HTTP protocol to be used
     * @param body          the body of the request, must include CRLF character for newline
     * @return              a string containing the HTTP response
     */
    public void executeRequest(String method, String requestURI, String protocol, String body) throws IOException, URISyntaxException {
        if (this.httpSocket == null) return;

        // Send request
        String path = HTTPUtil.parsePath(requestURI);
        HTTPRequest request = new HTTPRequest(this.host, method, path, protocol, body);
        request.initiateRequest(httpSocket.getOutputStream());

        HTTPResponse response = new HTTPResponse();
        response.handleResponse(httpSocket.getInputStream());


    }

    private void close() {
        try {
            this.httpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
