package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.header.HTTPHeader;

public abstract class HTTPMessage {

    protected HTTPHeader header;
    protected HTTPBody body;

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
}
