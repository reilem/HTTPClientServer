package com.reinert.common;

import javafx.util.Pair;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HTTPInputStream {

    private final InputStream inputStream;

    HTTPInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HTTPHeader getHeader() throws IOException {
        String[] firstLine = getNextLine().split(" ");
        String protocol = firstLine[0];
        int status = Integer.parseInt(firstLine[1]);
        HTTPHeader header = new HTTPHeader(HTTPProtocol.getProtocolFor(protocol), HTTPStatus.getStatusFor(status));
        Pair<HTTPField, Object> nextEntry;
        while ((nextEntry = getNextHeaderLine()) != null) {
            header.addField(nextEntry.getKey(), nextEntry.getValue());
        }
        return header;
    }

    public HTTPBody getBody(int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        BufferedInputStream in = new BufferedInputStream(inputStream);
        int length = 0;
        while (length < bufferSize) {
            int nextByteLen = in.read(buffer, length, bufferSize - length);
            if (nextByteLen == -1) break;
            length += nextByteLen;
        }
        return new HTTPBody(buffer);
    }

    private Pair<HTTPField, Object> getNextHeaderLine() throws IOException {
        String nextLine = this.getNextLine();
        if (nextLine.equals(HTTPUtil.CRLF)) return null;
        String[] split = nextLine.split(": ");
        try {
            HTTPField f = HTTPField.getFieldFor(split[0].trim());
            return new Pair<>(f, f.parseValueString(split[1].trim()));
        } catch (IllegalArgumentException e) {
            return new Pair<>(null, null);
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
