package com.reinert.common.HTTP.header;

import com.reinert.common.HTTP.HTTPProtocol;
import com.reinert.common.HTTP.HTTPStatus;

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
