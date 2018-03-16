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
import java.util.HashMap;

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
    public void executeRequest(HTTPMethod method, URI uri, HTTPProtocol protocol, HTTPBody requestBody, HashMap<HTTPField, Object> extraHeaderFields) throws IOException, URISyntaxException {
        if (this.httpSocket == null) return;
        if (this.httpSocket.isClosed()) { System.out.println("Socket is closed."); return; }

        // Create request header
        HTTPRequestHeader requestHeader = new HTTPRequestHeader(method, uri.getPath(), protocol);
        if (protocol.equals(HTTPProtocol.HTTP_1_1)) requestHeader.addField(HTTPField.HOST, uri.getHost());
        // Create request object and send it
        HTTPRequest request = new HTTPRequest(requestHeader, requestBody);
        request.sendRequest(this.httpSocket.getOutputStream());
        // Create response object and load in response data
        HTTPResponse response = new HTTPResponse();
        response.fetchResponse(this.httpSocket.getInputStream(), !requestHeader.getMethod().equals(HTTPMethod.HEAD));

        HTTPResponseHeader responseHeader = response.getHeader();
        HTTPBody responseBody = response.getBody();
        // Write out the data to file if not null
        if (responseBody != null) responseBody.writeToFile(makeClientFilePath(uri));

        // Check if redirection is needed
        if (requestHeader.getMethod().equals(HTTPMethod.GET) && responseHeader.getStatus().equals(HTTPStatus.CODE_302)) {
            String location = (String)requestHeader.getFieldValue(HTTPField.LOCATION);
            this.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI(location), protocol, null, null);
        } else {
            System.out.println("Response received."+HTTPUtil.NEW_LINE);
            System.out.println(responseHeader.toString());
        }

        // Close connection if needed
        boolean keepAlive = responseHeader.keepConnectionAlive();
        if (!keepAlive) {
            this.httpSocket.close();
        }

        if (responseBody != null) {
            // Check the content type
            ContentType contentType = (ContentType)responseHeader.getFieldValue(HTTPField.CONTENT_TYPE);
            String charSet = null;
            String type = null;
            String ext = null;
            if (contentType != null) {
                charSet = contentType.getCharSet();
                type = contentType.getType();
                ext = contentType.getExtension();
            }
            // Print results with given charset
            responseBody.printData(charSet);
            if (type != null && type.equals("text")) {
                // If file extension is html
                if (ext != null && ext.equals("html") && keepAlive) {
                    // Parse the sources from image tags
                    ArrayList<String> extraPaths = HTMLUtil.getImageURLs(responseBody.getAsString(charSet));
                    for (String imagePath : extraPaths) {
                        this.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI(uri.getHost()+"/"+imagePath), protocol, null, null);
                    }
                }
            }
        }
    }

    private String makeClientFilePath(URI uri) {
        return CLIENT_DIR + HTTPUtil.makeFilePathFromURI(uri);
    }
}
