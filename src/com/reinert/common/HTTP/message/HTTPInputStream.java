package com.reinert.common.HTTP.message;

import com.reinert.common.HTTP.*;
import com.reinert.common.HTTP.header.HTTPHeader;
import com.reinert.common.HTTP.header.HTTPRequestHeader;
import com.reinert.common.HTTP.header.HTTPResponseHeader;
import javafx.util.Pair;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HTTPInputStream {

    private final InputStream inputStream;

    HTTPInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HTTPResponseHeader getResponseHeader() throws IOException {
        String[] firstLine = getNextLine().split(" ");
        String protocol = firstLine[0].trim();
        int status = Integer.parseInt(firstLine[1]);
        HTTPResponseHeader header = new HTTPResponseHeader(HTTPProtocol.parseProtocol(protocol), HTTPStatus.getStatusFor(status));
        fillHeaderFields(header);
        return header;
    }

    public HTTPRequestHeader getRequestHeader() throws IOException {
        String[] firstLine = getNextLine().split(" ");
        String method = firstLine[0].trim();
        String path = firstLine[1].trim();
        String protocol = firstLine[2].trim();
        HTTPRequestHeader header = new HTTPRequestHeader(HTTPMethod.parseMethod(method), path, HTTPProtocol.parseProtocol(protocol));
        fillHeaderFields(header);
        return header;
    }

    public void fillHeaderFields(HTTPHeader header) throws IOException {
        Pair<HTTPField, Object> nextEntry;
        while ((nextEntry = getNextHeaderLine()) != null) {
            HTTPField key = nextEntry.getKey();
            Object val = nextEntry.getValue();
            header.addField(key, val);
        }
    }

    public HTTPBody getBody(Integer bufferSize) throws IOException {
        byte[] bodyData;
        if (bufferSize != null) bodyData = getBufferedBody(bufferSize);
        else bodyData = getChunkBody();
        return new HTTPBody(bodyData);
    }

    private byte[] getChunkBody() throws IOException {
        int next;
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        while ((next = inputStream.read()) != -1) {
            byteArray.write(next);
            byte[] bytes = byteArray.toByteArray();
            int l = bytes.length;
            if (l > 5 && bytes[l-4] == 13 && bytes[l-3] == 10 && bytes[l-2] == 13 && bytes[l-1] == 10) break;
        }
        return byteArray.toByteArray();
    }

    private byte[] getBufferedBody(int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        BufferedInputStream in = new BufferedInputStream(inputStream);
        int length = 0;
        while (length < bufferSize) {
            int nextByteLen = in.read(buffer, length, bufferSize - length);
            if (nextByteLen == -1) break;
            length += nextByteLen;
        }
        return buffer;
    }

    private Pair<HTTPField, Object> getNextHeaderLine() throws IOException {
        String nextLine = this.getNextLine();
        if (nextLine.equals(HTTPUtil.CRLF)) return null;
        String[] split = nextLine.split(": ");
        String headerField = split[0].trim();
        String headerValue = split[1].trim();
        try {
            HTTPField f = HTTPField.getFieldFor(headerField);
            return new Pair<>(f, f.parseValueString(headerValue));
        } catch (IllegalArgumentException e) {
            return new Pair<>(HTTPField.OTHER, nextLine.replace("\r\n", ""));
        }
    }

    private String getNextLine() throws IOException {
        StringBuilder string = new StringBuilder();
        while (!string.toString().endsWith(HTTPUtil.CRLF)) {
            string.append((char)this.inputStream.read());
        }
        return string.toString();
    }

}
