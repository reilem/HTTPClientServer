package com.reinert.client;

import com.reinert.common.HTMLUtil;
import com.reinert.common.HTTPUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
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
        System.out.println("Executing request...");
        // Create a response string builder
        StringBuilder response = new StringBuilder();
        // Open request & response streams
        BufferedWriter requestOutput = new BufferedWriter(new OutputStreamWriter(httpSocket.getOutputStream()));
        InputStream responseInput = httpSocket.getInputStream();

        // Write the main request line to output stream
        requestOutput.write(getRequestLine(method, requestURI, protocol));
        // If HTTP/1.1 include a host header
        if (protocol.equals("HTTP/1.1")) requestOutput.write(getHostHeader());
        // If body is given, include it
        if (body != null && !body.isEmpty()) {
            requestOutput.write(HTTPUtil.CRLF);
            requestOutput.write(body);
            requestOutput.write(HTTPUtil.CRLF);
        }
        requestOutput.write(HTTPUtil.CRLF);
        requestOutput.flush();
        System.out.println("Request sent...");

        Boolean connectionOpen = null;
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
        BufferedInputStream in = new BufferedInputStream(responseInput);
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
            // Find any <img /> tags if html
            if (fileType.equals("html")) {
                ArrayList<String> imagePaths = HTMLUtil.getImageURLs(new String(buffer, charSet));
                System.out.println("Response received...");
                System.out.println(response);
                for (String imagePath : imagePaths) {
                    System.out.println(imagePath);
                    this.executeRequest("GET", this.host+"/"+imagePath, protocol, null);
                }
            }
        }
        String param = HTTPUtil.parsePath(requestURI);
        String file = param.equals("/") ? "/index.html" : param;
        String filePath = "res-client/" + file;
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(buffer);
    }

    public void closeClient() {
        try {
            this.httpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRequestLine(String method, String uri, String protocol) {
        String path = "/";
        if (protocol.equals("HTTP/1.1")) {
            try {
                path = HTTPUtil.parsePath(uri);
            } catch (MalformedURLException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else path = uri;
        return (method + " " + path + " " + protocol + HTTPUtil.CRLF);
    }

    private String getHostHeader() {
        return  ("Host: " + this.host + HTTPUtil.CRLF);
    }

}
