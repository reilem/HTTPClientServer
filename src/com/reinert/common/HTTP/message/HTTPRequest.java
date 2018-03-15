package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.header.HTTPRequestHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HTTPRequest extends HTTPMessage {

    private HTTPRequestHeader header;

    public HTTPRequest() {}

    public HTTPRequest(HTTPRequestHeader header, HTTPBody body) {
        super(header, body);
        this.header = header;
    }

    public void sendRequest(OutputStream outputStream) throws IOException {
        HTTPOutputStream httpOutputStream = new HTTPOutputStream(outputStream);
        httpOutputStream.sendRequest(this.header, this.body);
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
}
