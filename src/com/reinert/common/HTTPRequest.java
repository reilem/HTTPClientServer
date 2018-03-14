package com.reinert.common;

import java.io.*;

public class HTTPRequest {

    private final String host;
    private final String method;
    private final String path;
    private final String protocol;
    private final String body;

    public HTTPRequest(String host, String method, String path, String protocol, String body) {
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
        if (this.protocol.equals("HTTP/1.1")) requestOutput.write(makeHostHeader());
        if (this.requiresUserInput()) {
            String requestBody;
            if (this.body == null) requestBody = getUserInput();
            else requestBody = this.body;
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
        System.out.println("Input done.");
        System.out.println(input);
        return input.toString();
    }

    private boolean requiresUserInput() {
        return this.method.matches("POST|PUT");
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
