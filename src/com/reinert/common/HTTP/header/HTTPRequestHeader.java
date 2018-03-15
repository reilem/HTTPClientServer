package com.reinert.common.HTTP.header;

import com.reinert.common.HTTP.HTTPMethod;
import com.reinert.common.HTTP.HTTPProtocol;

public class HTTPRequestHeader extends HTTPHeader{

    private final HTTPMethod method;
    private final String path;

    public HTTPRequestHeader(HTTPMethod method, String path, HTTPProtocol protocol) {
        super(protocol);
        this.method = method;
        this.path = path;
    }

    public HTTPMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    @Override
    String headerTopLine() {
        return method.toString() + " " + path + " " + protocol.toString();
    }
}
