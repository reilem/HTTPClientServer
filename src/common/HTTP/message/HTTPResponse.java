package common.HTTP.message;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPStatus;
import common.HTTP.header.HTTPResponseHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HTTPResponse extends HTTPMessage {

    private HTTPResponseHeader header;

    public HTTPResponse() {}

    public HTTPResponse(HTTPResponseHeader header, HTTPBody body) {
        super(body);
        this.header = header;
    }

    @Override
    public HTTPResponseHeader getHeader() {
        return header;
    }

    public void fetchResponse(InputStream inputStream, boolean fetchBody) throws IOException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        fetchResponseHeader(httpInputStream);
        // Fetch the body if needed
        if (fetchBody) this.fetchBody(httpInputStream);
    }

    public void sendResponse(OutputStream outputStream) throws IOException {
        // Create http output stream
        HTTPOutputStream httpOutputStream = new HTTPOutputStream(outputStream);
        // Send the response header and body
        httpOutputStream.sendMessage(this.header, this.body);
    }

    private void fetchResponseHeader(HTTPInputStream httpInputStream) throws IOException {
        String[] firstLine = httpInputStream.getNextLine().split(" ");
        String protocol = firstLine[0].trim();
        int status = Integer.parseInt(firstLine[1]);
        this.header = new HTTPResponseHeader(HTTPProtocol.parseProtocol(protocol), HTTPStatus.getStatusFor(status));
        httpInputStream.fillHeaderFields(this.header);
    }
}
