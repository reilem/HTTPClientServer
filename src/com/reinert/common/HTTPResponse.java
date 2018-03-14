package com.reinert.common;

import java.io.IOException;
import java.io.InputStream;

public class HTTPResponse {

    private HTTPHeader header = null;
    private HTTPBody body = null;

    public void handleResponse(InputStream responseInput) throws IOException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(responseInput);
        // Fetch the header from the input stream
        this.header = httpInputStream.getHeader();
        // Get the content length from the header
        int bufferSize = (Integer)this.header.getFieldValue(HTTPField.CONTENT_LENGTH);
        // Fetch the body from the input stream
        this.body = httpInputStream.getBody(bufferSize);
    }

    public HTTPHeader getHeader() {
        return header;
    }

    public HTTPBody getBody() {
        return body;
    }
}
