package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.header.HTTPHeader;
import com.reinert.common.HTTP.header.HTTPResponseHeader;

import java.io.IOException;
import java.io.InputStream;

public class HTTPResponse extends HTTPMessage {

    private HTTPResponseHeader header;

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
        // Fetch the body
        this.fetchBody(httpInputStream);
    }

    public void sendResponse(HTTPHeader header, HTTPBody body) {

    }

    @Override
    public HTTPResponseHeader getHeader() {
        return header;
    }
}
