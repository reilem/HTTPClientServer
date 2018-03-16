package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.*;
import com.reinert.common.HTTP.header.HTTPRequestHeader;

import java.io.*;

public class HTTPRequest extends HTTPMessage {

    private HTTPRequestHeader header;

    public HTTPRequest() {}

    public HTTPRequest(HTTPRequestHeader header, HTTPBody body) {
        super(body);
        this.header = header;
    }

    public void fetchRequest(InputStream inputStream) throws IOException, ContentLengthRequiredException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        this.header = httpInputStream.getRequestHeader();
        HTTPMethod method =  this.header.getMethod();
        Object length = this.header.getFieldValue(HTTPField.CONTENT_LENGTH);
        Object encoding = this.header.getFieldValue(HTTPField.TRANSFER_ENCODING);
        if (method.requiresBody() && length == null && (encoding == null || !encoding.equals(HTTPUtil.CHUNKED))) {
            throw new ContentLengthRequiredException();
        }
        this.fetchBody(httpInputStream);
    }

    public void sendRequest(OutputStream outputStream) throws IOException {
        HTTPOutputStream httpOutputStream = new HTTPOutputStream(outputStream);
        HTTPBody requestBody = null;
        if (this.header.getMethod().requiresBody()) {
            if (body == null) requestBody = getUserInput();
            else requestBody = body;
            this.header.addField(HTTPField.CONTENT_LENGTH, requestBody.getData().length);
        }
        httpOutputStream.sendMessage(this.header, requestBody);
        System.out.println("Request sent...");
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
