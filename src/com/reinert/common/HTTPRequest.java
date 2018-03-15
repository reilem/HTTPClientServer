package com.reinert.common;

import java.io.*;

public class HTTPRequest {

    private final HTTPProtocol protocol;
    private final HTTPBody body;
    private final HTTPMethod method;
    private final String path;
    private final String host;

    public HTTPRequest(String host, HTTPMethod method, String path, HTTPProtocol protocol, HTTPBody body) {
        this.host = host;
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.body = body;
    }

    public void initiateRequest(OutputStream output) throws IOException {
        // Create a buffered writer
        BufferedWriter requestOutput = new BufferedWriter(new OutputStreamWriter(output));
        // Write the main request line to output stream
        requestOutput.write(makeRequestLine());
        // If HTTP/1.1 include a host header
        if (this.protocol.equals(HTTPProtocol.HTTP_1_1)) requestOutput.write(makeHostHeader());
        if (this.method.requiresBody()) {
            String requestBody;
            if (this.body == null) requestBody = getUserInput();
            else requestBody = this.body.getAsString(null);
            requestOutput.write(makeContentHeader(requestBody.getBytes().length));
            requestOutput.write(HTTPUtil.CRLF);
            requestOutput.write(requestBody);
        }
        // Write final line
        requestOutput.write(HTTPUtil.CRLF);
        // Flush the output
        requestOutput.flush();
        System.out.println("Request sent...");
    }

    private String getUserInput() throws IOException {
        System.out.println("Please input request body:");
        BufferedReader inputRead = new BufferedReader(new InputStreamReader(System.in));
        String nextInput;
        StringBuilder input = new StringBuilder();
        while (!(nextInput = inputRead.readLine()).equals("")) {
            input.append(nextInput);
            input.append(HTTPUtil.CRLF);
        }
        return input.toString();
    }

    private String makeContentHeader(int length) {
        return ("Content-Length: " + length + HTTPUtil.CRLF +
        "Content-Type: text/txt; charset=UTF-8"+ HTTPUtil.CRLF);
    }

    private String makeRequestLine() {
        return (this.method + " " + this.path + " " + this.protocol + HTTPUtil.CRLF);
    }

    private String makeHostHeader() {
        return  ("Host: " + this.host + HTTPUtil.CRLF);
    }
}
