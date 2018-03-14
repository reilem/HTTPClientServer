package com.reinert.client;

import com.reinert.common.HTMLUtil;
import com.reinert.common.HTTPRequest;
import com.reinert.common.HTTPUtil;

import java.io.*;
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
     * @param body          the body of the request, must include CRLF character for newline
     * @return              a string containing the HTTP response
     */
    public void executeRequest(String method, String requestURI, String protocol, String body) throws IOException, URISyntaxException {
        if (this.httpSocket == null) return;

        // Send request
        String path = HTTPUtil.parsePath(requestURI);
        HTTPRequest request = new HTTPRequest(this.host, method, path, protocol, body);
        request.initiateRequest(httpSocket.getOutputStream());

        // Create a response string builder
        StringBuilder response = new StringBuilder();
        // Open request & response streams
        InputStream responseInput = httpSocket.getInputStream();

        Boolean connectionOpen = false;
        int contentLength = 0;
        String contentType = "text";
        String charSet = "UTF-8";
        StringBuilder line = new StringBuilder();
        int next = responseInput.read();
        boolean previousCR = false;
        while (next != -1) {
            char nextChar = (char)next;
            if (!previousCR && nextChar == HTTPUtil.CR) {
                previousCR = true;
            } else if (previousCR && nextChar == HTTPUtil.LF) {
                if (line.length() == 0) break;
                int index = line.toString().indexOf(':');
                if (index != -1) {
                    String tag = line.substring(0, index+1).toLowerCase();
                    String value = line.substring(index+2).toLowerCase();
                    switch (tag) {
                        case "content-type:":
                            int t = value.indexOf(';');
                            if (t == -1) contentType = value;
                            else {
                                int l = value.indexOf('=');
                                contentType = value.substring(0,t);
                                charSet = value.substring(l+1).toUpperCase();
                            }
                            break;
                        case "content-length:":
                            contentLength = Integer.parseInt(value);
                            break;
                        case "connection:":
                            connectionOpen = value.equals("keep-alive");
                            break;
                    }
                }
                response.append(line);
                response.append(HTTPUtil.NEW_LINE);
                line.setLength(0);
                previousCR = false;
            } else {
                line.append(nextChar);
            }
            next = responseInput.read();
        }
        response.append(HTTPUtil.NEW_LINE);
        System.out.println("Header received: " + HTTPUtil.NEW_LINE + response);
        response.setLength(0);


        int bufferSize = contentLength;
        byte[] buffer = new byte[bufferSize];
        BufferedInputStream in = new BufferedInputStream(httpSocket.getInputStream());
        int length = 0;
        while (length < contentLength) {
            int nextByteLen = in.read(buffer, length, contentLength - length);
            if (nextByteLen == -1) break;
            length += nextByteLen;
        }

        // Parse the received byte array
        if (contentType.startsWith("text")) {
            // Get index for secondary file type
            int index = contentType.indexOf('/');
            // Get file type
            String fileType = contentType.substring(index + 1);
            // MAke the string contents
            String responseBody = new String(buffer, charSet);
            System.out.println("Response received...");
            System.out.println(responseBody);
            // IF file type is html
            if (fileType.equals("html")) {
                // Parse the sources from image tags
                ArrayList<String> extraPaths = HTMLUtil.getImageURLs(responseBody);
                for (String imagePath : extraPaths) {
                    System.out.println(imagePath);
                    this.executeRequest("GET", this.host+"/"+imagePath, protocol, null);
                }
            }
        }
        System.out.println(new String(buffer));
        // Write out the file
        String param = HTTPUtil.parsePath(requestURI);
        String file = param.equals("/") ? "/index.html" : param;
        String filePath = "res-client/" + file;
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(buffer);
        // Close connection if needed
        if (!connectionOpen) {
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
