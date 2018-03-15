package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.*;
import com.reinert.common.HTTP.header.HTTPRequestHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class HTTPOutputStream {

    private final OutputStream outputStream;

    public HTTPOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendRequest(HTTPRequestHeader header, HTTPBody body) throws IOException {
        HTTPMethod method = header.getMethod();
        this.outputStream.write(header.toString().getBytes());
        if (method.requiresBody()) {
            byte[] requestBody;
            if (body == null) requestBody = getUserInput().getBytes();
            else requestBody = body.getData();
            this.outputStream.write(HTTPUtil.CRLF.getBytes());
            this.outputStream.write(requestBody);
        }
        // Write final line
        this.outputStream.write(HTTPUtil.CRLF.getBytes());
        // Flush the output
        this.outputStream.flush();
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

    private void sendLine(String s) throws IOException {
        byte[] bytes = s.getBytes();
        for (byte b : bytes) {
            this.outputStream.write(b);
        }
        this.outputStream.write(HTTPUtil.CRLF.getBytes());
    }
}
