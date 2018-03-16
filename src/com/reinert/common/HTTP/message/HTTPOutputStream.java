package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.HTTPUtil;
import com.reinert.common.HTTP.header.HTTPHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class HTTPOutputStream {

    private final OutputStream outputStream;

    public HTTPOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendMessage(HTTPHeader header, HTTPBody body) throws IOException {
        this.outputStream.write(header.toString().getBytes());
        if (body != null) {
            this.outputStream.write(HTTPUtil.CRLF.getBytes());
            this.outputStream.write(body.getData());
        }
        // Write final line
        this.outputStream.write(HTTPUtil.CRLF.getBytes());
        // Flush the output
        this.outputStream.flush();
    }



    private void sendLine(String s) throws IOException {
        byte[] bytes = s.getBytes();
        for (byte b : bytes) {
            this.outputStream.write(b);
        }
        this.outputStream.write(HTTPUtil.CRLF.getBytes());
    }
}
