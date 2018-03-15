package com.reinert.client;

import com.reinert.common.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class HTTPClient {

    private final String CLIENT_DIR = "res-client";

    private final Socket httpSocket;

    HTTPClient(int port, URI uri) throws IOException {
        // Create socket based on host name provided by uri
        this.httpSocket = new Socket(InetAddress.getByName(uri.getHost()), port);
    }

    /**
     * Executes a HTTP request with given parameters.
     * @param method        the HTTP method of the request
     * @param uri           the uri of the request
     * @param protocol      the HTTP protocol to be used
     * @param requestBody   the requestBody of the request, must include CRLF characters as newlines
     */
    public void executeRequest(HTTPMethod method, URI uri, HTTPProtocol protocol, HTTPBody requestBody) throws IOException, URISyntaxException {
        if (this.httpSocket == null) return;

        // Send request
        String path = uri.getPath();
        HTTPRequest request = new HTTPRequest(uri.getHost(), method, path, protocol, requestBody);
        request.initiateRequest(this.httpSocket.getOutputStream());

        HTTPResponse response = new HTTPResponse();
        response.handleResponse(this.httpSocket.getInputStream());

        HTTPHeader header = response.getHeader();
        HTTPBody responseBody = response.getBody();

        // Write out the file
        responseBody.writeToFile(makeFilePath(uri));

        // Check if redirection is needed
        if (header.getStatus().equals(HTTPStatus.CODE_302)) {
            String location = (String)header.getFieldValue(HTTPField.LOCATION);
            this.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI(location), protocol, null);
        } else {
            System.out.println("Response received."+HTTPUtil.NEW_LINE);
            System.out.println(header.toString());
            // Check the content type
            ContentType contentType = (ContentType)header.getFieldValue(HTTPField.CONTENT_TYPE);
            String charSet = contentType.getCharSet();
            // Parse the received byte array
            if (contentType.getType().equals("text")) {
                // Print results
                responseBody.printData(charSet);
                // If file extension is html
                if (contentType.getExtension().equals("html")) {
                    // Parse the sources from image tags
                    ArrayList<String> extraPaths = HTMLUtil.getImageURLs(responseBody.getAsString(charSet));
                    for (String imagePath : extraPaths) {
                        this.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI(uri.getHost()+"/"+imagePath), protocol, null);
                    }
                }
            }
        }

        // Close connection if needed
        Boolean headConnect = (Boolean)header.getFieldValue(HTTPField.CONNECTION);
        Boolean keepAlive = (protocol.equals(HTTPProtocol.HTTP_1_0) && headConnect != null && headConnect) ||
                (protocol.equals(HTTPProtocol.HTTP_1_1) && (headConnect == null || headConnect));
        if (!keepAlive) {
            this.httpSocket.close();
        }
    }

    private String makeFilePath(URI uri) {
        String param = uri.getPath();
        String file = param.equals("/") ? "/index.html" : param;
        return CLIENT_DIR + file;
    }
}
