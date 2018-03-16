package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.HTTPField;
import com.reinert.common.HTTP.header.HTTPHeader;

import java.io.IOException;

public abstract class HTTPMessage {

    private static final String CHUNKED = "chunked";

    HTTPBody body;

    HTTPMessage() {}

    HTTPMessage(HTTPBody body) {
        this.body = body;
    }

    abstract HTTPHeader getHeader();

    public HTTPBody getBody() {
        return body;
    }

    void fetchBody(HTTPInputStream httpInputStream) throws IOException {
        // Get the content length from the header
        Integer cLen = (Integer)this.getHeader().getFieldValue(HTTPField.CONTENT_LENGTH);
        String encoding = (String)this.getHeader().getFieldValue(HTTPField.TRANSFER_ENCODING);
        if (encoding != null && encoding.equals(CHUNKED)) {
            this.body = httpInputStream.getBody(null);
        } else if (cLen != null) {
            this.body = httpInputStream.getBody(cLen);
        } else {
            this.body = null;
        }
    }
}
