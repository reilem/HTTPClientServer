package common.HTTP.message;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPField;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPUtil;
import common.HTTP.header.HTTPHeader;

import java.io.IOException;

/**
 * Class for HTTP Messages.
 */
public abstract class HTTPMessage {

    // The HTTPBody of the current message
    HTTPBody body;

    /**
     * A constructor for a HTTPMessage requiring no parameters.
     */
    HTTPMessage() {}

    /**
     * A constructor for a HTTP Message.
     * @param body The body of the current message.
     */
    HTTPMessage(HTTPBody body) {
        this.body = body;
    }

    /**
     * Abstract method to get the header of the current HTTP message. The Header may be a HTTPResponseHeader or a
     * HTTPRequestHeader
     */
    abstract HTTPHeader getHeader();

    /**
     * Gets the body of the current message.
     */
    public HTTPBody getBody() {
        return body;
    }

    /**
     * Fetch the body available on the given input stream.
     * @param httpInputStream   HTTPInputStream on which the body is available.
     * @throws IOException      If anything goes wrong during fetching the body.
     */
    void fetchBody(HTTPInputStream httpInputStream) throws IOException {
        // Get the content length from the current header
        Integer cLen = (Integer)this.getHeader().getFieldValue(HTTPField.CONTENT_LENGTH);
        // Get the encoding from the current header
        Object encoding = this.getHeader().getFieldValue(HTTPField.TRANSFER_ENCODING);
        // If encoding is not null and is equal to CHUNKED and the current protocol is 1.1
        if (encoding != null && encoding.equals(HTTPUtil.CHUNKED) && this.getHeader().getProtocol().equals(HTTPProtocol.HTTP_1_1)) {
            // Get the body in chunked mode from the given input stream
            this.body = httpInputStream.getChunkedBody();
        } else if (cLen != null) {
            // Otherwise if a valid content length is given, get the body in buffered mode
            this.body = httpInputStream.getBufferedBody(cLen);
        } else {
            // Other set the body to null.
            this.body = null;
        }
    }
}
