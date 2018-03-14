package com.reinert.client;

import com.reinert.common.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;

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
     * @param requestBody   the requestBody of the request, must include CRLF characters as newlines
     * @return              a string containing the HTTP response
     */
    public void executeRequest(String method, String requestURI, String protocol, String requestBody) throws IOException, URISyntaxException {
        if (this.httpSocket == null) return;

        // Send request
        String path = HTTPUtil.parsePath(requestURI);
        HTTPRequest request = new HTTPRequest(this.host, method, path, protocol, requestBody);
        request.initiateRequest(httpSocket.getOutputStream());

        HTTPResponse response = new HTTPResponse();
        response.handleResponse(httpSocket.getInputStream());

        HTTPHeader header = response.getHeader();
        HTTPBody responseBody = response.getBody();

        ContentType contentType = (ContentType)header.getFieldValue(HTTPField.CONTENT_TYPE);
        String charSet = contentType.getCharSet();
        // Parse the received byte array
        if (contentType.getType().equals("text")) {
            // Print results
            System.out.println("Response received...");
            responseBody.printData(charSet);
            // If file extension is html
            if (contentType.getExtension().equals("html")) {
                // Parse the sources from image tags
                ArrayList<String> extraPaths = HTMLUtil.getImageURLs(responseBody.getAsString(charSet));
                for (String imagePath : extraPaths) {
                    this.executeRequest("GET", this.host+"/"+imagePath, protocol, null);
                }
            }
        }

        // Write out the file
        String param = HTTPUtil.parsePath(requestURI);
        String file = param.equals("/") ? "/index.html" : param;
        String filePath = "res-client/" + file;
        responseBody.writeToFile(filePath);
        // Close connection if needed
        Boolean connection = (Boolean)header.getFieldValue(HTTPField.CONNECTION);
        if (connection == null || !connection) {
            this.close();
        }

    }

    private void close() {
        try {
            this.httpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
