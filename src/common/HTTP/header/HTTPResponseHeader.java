package common.HTTP.header;

import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPStatus;

public class HTTPResponseHeader extends HTTPHeader {
    private final HTTPStatus status;

    public HTTPResponseHeader(HTTPProtocol protocol, HTTPStatus status) {
        super(protocol);
        this.status = status;
    }

    public HTTPStatus getStatus() {
        return status;
    }

    @Override
    String headerTopLine() {
        return protocol.toString() + " " + status.toString();
    }
}
