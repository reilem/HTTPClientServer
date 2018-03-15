package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.HTTPField;
import com.reinert.common.HTTP.header.HTTPHeader;
import com.reinert.common.HTTP.header.HTTPResponseHeader;

import java.io.IOException;
import java.io.InputStream;

public class HTTPResponse extends HTTPMessage {

    private HTTPResponseHeader header;
    private HTTPBody body;

    public HTTPResponse() {}

    public HTTPResponse(HTTPResponseHeader header, HTTPBody body) {
        super(header, body);
        this.header = header;
    }

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

    @Override
    public HTTPResponseHeader getHeader() {
        return header;
    }
}
