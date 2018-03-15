package com.reinert.common.HTTP;

import com.reinert.common.HTTP.header.HTTPRequestHeader;

import java.io.*;

public class HTTPRequest {

    private HTTPRequestHeader header;
    private HTTPBody body;

    public HTTPRequest(HTTPRequestHeader header, HTTPBody body) {
        this.header = header;
        this.body = body;
    }

    public void sendRequest(OutputStream output) throws IOException {
        // Create a buffered writer
        BufferedWriter requestOutput = new BufferedWriter(new OutputStreamWriter(output));
        HTTPMethod method = header.getMethod();
        // Write the header to the output
        requestOutput.write(header.toString());
        if (method.requiresBody()) {
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

    public void fetchRequest() {

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
        return (this.header.getMethod() + " " + this.header.getPath() + " " + this.header.getProtocol() + HTTPUtil.CRLF);
    }
}
