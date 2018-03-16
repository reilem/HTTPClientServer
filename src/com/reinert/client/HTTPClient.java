package com.reinert.client;

import com.reinert.common.HTML.HTMLUtil;
import com.reinert.common.HTTP.*;
import com.reinert.common.HTTP.header.HTTPRequestHeader;
import com.reinert.common.HTTP.header.HTTPResponseHeader;
import com.reinert.common.HTTP.message.HTTPRequest;
import com.reinert.common.HTTP.message.HTTPResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class HTTPClient {

    private static final String CLIENT_DIR = "res-client";

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
        if (this.httpSocket.isClosed()) { System.out.println("Socket is closed."); return; }

        // Send request
        String path = uri.getPath();
        HTTPRequestHeader requestHeader = new HTTPRequestHeader(method, path, protocol);
        requestHeader.addField(HTTPField.HOST, uri.getHost());
        HTTPRequest request = new HTTPRequest(requestHeader, requestBody);
        request.sendRequest(this.httpSocket.getOutputStream());

        HTTPResponse response = new HTTPResponse();
        response.fetchResponse(this.httpSocket.getInputStream(), !requestHeader.getMethod().equals(HTTPMethod.HEAD));

        HTTPResponseHeader responseHeader = response.getHeader();
        HTTPBody responseBody = response.getBody();

        // Write out the data to file if not null
        if (responseBody != null) responseBody.writeToFile(makeClientFilePath(uri));

        // Check if redirection is needed
        if (requestHeader.getMethod().equals(HTTPMethod.GET) && responseHeader.getStatus().equals(HTTPStatus.CODE_302)) {
            String location = (String)requestHeader.getFieldValue(HTTPField.LOCATION);
            this.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI(location), protocol, null);
        } else {
            System.out.println("Response received."+HTTPUtil.NEW_LINE);
            System.out.println(responseHeader.toString());
        }

        // Close connection if needed
        Connection headConnect = ((Connection)requestHeader.getFieldValue(HTTPField.CONNECTION));
        boolean keepAlive;
        if (headConnect == null) {
            keepAlive = protocol.equals(HTTPProtocol.HTTP_1_1);
        } else {
            keepAlive = headConnect.keepAlive();
        }
        if (!keepAlive) {
            this.httpSocket.close();
        }

        // Check the content type
        ContentType contentType = (ContentType)requestHeader.getFieldValue(HTTPField.CONTENT_TYPE);
        if (contentType == null) return;
        String charSet = contentType.getCharSet();
        if (contentType.getType().equals("text")) {
            // Print results
            responseBody.printData(charSet);
            // If file extension is html
            if (contentType.getExtension().equals("html") && keepAlive) {
                // Parse the sources from image tags
                ArrayList<String> extraPaths = HTMLUtil.getImageURLs(responseBody.getAsString(charSet));
                for (String imagePath : extraPaths) {
                    this.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI(uri.getHost()+"/"+imagePath), protocol, null);
                }
            }
        }
    }

    private String makeClientFilePath(URI uri) {
        return CLIENT_DIR + HTTPUtil.makeFilePathFromURI(uri);
    }
}
