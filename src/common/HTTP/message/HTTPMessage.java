package common.HTTP.message;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPField;
import common.HTTP.HTTPProtocol;
import common.HTTP.exceptions.ContentLengthRequiredException;
import common.HTTP.header.HTTPHeader;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for HTTP Messages.
 */
public abstract class HTTPMessage {

    //
    private static final String CHUNKED = "chunked";

    // The HTTPBody of the current message
    private HTTPBody body;

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
     * Abstract method to print the header of the current HTTP message.
     */
    public abstract void printHeader();

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
     * @param httpInputStream                   HTTPInputStream on which the body is available.
     * @throws IOException                      If anything goes wrong during fetching the body.
     * @throws ContentLengthRequiredException   If content length is required and not given.
     */
    void fetchBody(HTTPInputStream httpInputStream) throws IOException, ContentLengthRequiredException {
        // Get the protocol from current headers
        HTTPProtocol protocol = this.getHeader().getProtocol();
        // Get the content length from the current header
        Integer cLen = (Integer)this.getHeader().getFieldValue(HTTPField.CONTENT_LENGTH);
        // Get the encoding from the current header
        Object encoding = this.getHeader().getFieldValue(HTTPField.TRANSFER_ENCODING);
        // If encoding is not null and is equal to CHUNKED and the current protocol supports chunked
        if (encoding != null && encoding.equals(CHUNKED) && protocol.supportsChunked()) {
            // Get the body in chunked mode from the given input stream
            this.body = httpInputStream.getChunkedBody();
        } else if (cLen != null) {
            // Otherwise if a valid content length is given, get the body in buffered mode
            this.body = httpInputStream.getBufferedBody(cLen);
        } else {
            // Otherwise throw a content length required exception.
            throw new ContentLengthRequiredException();
        }
    }

    /**
     * Send the current message through the given output stream.
     * @param outputStream  The output stream on which the current message will be sent.
     * @throws IOException  If something goes wrong during transmission.
     */
    public void send(OutputStream outputStream) throws IOException {
        // Create http output stream
        HTTPOutputStream httpOutputStream = new HTTPOutputStream(outputStream);
        // Send the current message
        httpOutputStream.sendMessage(this);
    }
}
