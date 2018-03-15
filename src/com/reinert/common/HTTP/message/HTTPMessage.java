package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.HTTPField;
import com.reinert.common.HTTP.header.HTTPHeader;

import java.io.IOException;

public abstract class HTTPMessage {

    protected HTTPHeader header;
    HTTPBody body;

    public HTTPMessage(){
    }

    public HTTPMessage(HTTPHeader header, HTTPBody body) {
        this.header = header;
        this.body = body;
    }

    abstract HTTPHeader getHeader();

    public HTTPBody getBody() {
        return body;
    }

    protected void fetchBody(HTTPInputStream httpInputStream) throws IOException {
        // Get the content length from the header
        Object cLen = this.header.getFieldValue(HTTPField.CONTENT_LENGTH);
        Integer bufferSize = cLen != null ? (Integer)cLen : null;
        // Fetch the body from the input stream
        this.body = httpInputStream.getBody(bufferSize);
    }
}
