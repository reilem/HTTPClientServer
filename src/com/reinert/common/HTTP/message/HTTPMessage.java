package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.HTTPField;
import com.reinert.common.HTTP.HTTPUtil;
import com.reinert.common.HTTP.header.HTTPHeader;

import java.io.IOException;

public abstract class HTTPMessage {

    HTTPBody body;

    HTTPMessage() {}

    HTTPMessage(HTTPBody body) {
        this.body = body;
    }

    abstract HTTPHeader getHeader();

    public HTTPBody getBody() {
        return body;
    }

    public void fetchBody(HTTPInputStream httpInputStream) throws IOException {
        // Get the content length from the header
        Object cLen = this.getHeader().getFieldValue(HTTPField.CONTENT_LENGTH);
        Object encoding = this.getHeader().getFieldValue(HTTPField.TRANSFER_ENCODING);
        if (encoding != null && encoding.equals(HTTPUtil.CHUNKED)) {
            this.body = httpInputStream.getBody(null);
        } else if (cLen != null) {
            this.body = httpInputStream.getBody((Integer)cLen);
        } else {
            this.body = null;
        }
    }
}
