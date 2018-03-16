package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.HTTPUtil;
import com.reinert.common.HTTP.header.HTTPRequestHeader;

import java.io.*;

public class HTTPRequest extends HTTPMessage {

    private HTTPRequestHeader header;

    public HTTPRequest() {}

    public HTTPRequest(HTTPRequestHeader header, HTTPBody body) {
        super(body);
        this.header = header;
    }

    public void sendRequest(OutputStream outputStream) throws IOException {
        HTTPOutputStream httpOutputStream = new HTTPOutputStream(outputStream);
        if (this.header.getMethod().requiresBody()) {
            HTTPBody requestBody;
            if (body == null) requestBody = getUserInput();
            else requestBody = body;
            httpOutputStream.sendMessage(this.header, requestBody);
        }
        System.out.println("Request sent...");
    }

    public void fetchRequest(InputStream inputStream) throws IOException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        this.header = httpInputStream.getRequestHeader();
        // Fetch the body
        this.fetchBody(httpInputStream);
    }

    @Override
    public HTTPRequestHeader getHeader() { return header; }

    private HTTPBody getUserInput() throws IOException {
        System.out.println("Please input request body:");
        BufferedReader inputRead = new BufferedReader(new InputStreamReader(System.in));
        String nextInput;
        StringBuilder input = new StringBuilder();
        while (!(nextInput = inputRead.readLine()).equals("")) {
            input.append(nextInput);
            input.append(HTTPUtil.CRLF);
        }
        return new HTTPBody(input.toString().getBytes());
    }
}
