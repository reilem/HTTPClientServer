package com.reinert.common.HTTP;

import com.reinert.common.HTTP.header.HTTPHeader;
import com.reinert.common.HTTP.header.HTTPResponseHeader;

import java.io.IOException;
import java.io.InputStream;

public class HTTPResponse {

    private HTTPResponseHeader header = null;
    private HTTPBody body = null;

    public void fetchResponse(InputStream responseInput) throws IOException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(responseInput);
        // Fetch the header from the input stream
        this.header = httpInputStream.getResponseHeader();
        // Get the content length from the header
        Object cLen = this.header.getFieldValue(HTTPField.CONTENT_LENGTH);
        Integer bufferSize = cLen != null ? (Integer)cLen : null;
        // Fetch the body from the input stream
        this.body = httpInputStream.getBody(bufferSize);
    }

    public void sendResponse(HTTPHeader header, HTTPBody body) {

    }

    public HTTPResponseHeader getHeader() {
        return header;
    }

    public HTTPBody getBody() {
        return body;
    }
}
