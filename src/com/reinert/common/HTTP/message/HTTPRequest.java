package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.header.HTTPRequestHeader;

import java.io.IOException;
import java.io.OutputStream;

public class HTTPRequest extends HTTPMessage {

    private HTTPRequestHeader header;

    public HTTPRequest() {}

    public HTTPRequest(HTTPRequestHeader header, HTTPBody body) {
        super(header, body);
        this.header = header;
    }

    public void sendRequest(OutputStream outputStream) throws IOException {
        HTTPOutputStream httpOutputStream = new HTTPOutputStream(outputStream);
        httpOutputStream.sendRequest(this.header, this.body);
        System.out.println("Request sent...");
    }

    public void fetchRequest() {

    }

    @Override
    public HTTPRequestHeader getHeader() { return header; }
}
