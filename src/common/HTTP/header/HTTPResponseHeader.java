package common.HTTP.header;

import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPStatus;

/**
 * Class for HTTP response headers. Used for both sending responses server-side and receiving responses on the client-side.
 */
public class HTTPResponseHeader extends HTTPHeader {

    // HTTP Status from the current header's status-line.
    private final HTTPStatus status;

    /**
     * Constructor for a HTTP response header.
     */
    public HTTPResponseHeader(HTTPProtocol protocol, HTTPStatus status) {
        super(protocol);
        this.status = status;
    }

    /**
     * Gets the response header's status.
     */
    public HTTPStatus getStatus() {
        return status;
    }

    @Override
    String getHeaderMainLine() {
        return protocol.toString() + " " + status.toString();
    }
}
