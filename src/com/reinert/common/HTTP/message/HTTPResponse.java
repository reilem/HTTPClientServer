package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.header.HTTPResponseHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HTTPResponse extends HTTPMessage {

    private HTTPResponseHeader header;

    public HTTPResponse() {}

    public HTTPResponse(HTTPResponseHeader header, HTTPBody body) {
        super(body);
        this.header = header;
    }

    public void fetchResponse(InputStream inputStream, boolean fetchBody) throws IOException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        this.header = httpInputStream.getResponseHeader();
        // Fetch the body if needed
        if (fetchBody) this.fetchBody(httpInputStream);
    }

    public void sendResponse(OutputStream outputStream) throws IOException {
        // Create http output stream
        HTTPOutputStream httpOutputStream = new HTTPOutputStream(outputStream);
        // Send the response header and body
        httpOutputStream.sendMessage(this.header, this.body);
    }

    @Override
    public HTTPResponseHeader getHeader() {
        return header;
    }
}
