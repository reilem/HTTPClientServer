package common.HTTP.message;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPStatus;
import common.HTTP.header.HTTPResponseHeader;

import java.io.IOException;
import java.io.InputStream;

/**
 * A class for HTTPResponses. Includes methods for sending and fetching responses.
 */
public class HTTPResponse extends HTTPMessage {

    // The header of the current response
    private HTTPResponseHeader header;

    /**
     * Constructor for a HTTPResponse requiring no parameters
     */
    public HTTPResponse() {}

    /**
     * Constructor for a HTTPResponse.
     * @param header    Request header of the current request.
     * @param body      Body of the current request.
     */
    public HTTPResponse(HTTPResponseHeader header, HTTPBody body) {
        super(body);
        this.header = header;
    }

    @Override
    public HTTPResponseHeader getHeader() { return header; }

    /**
     * Fetch the response available on the given input stream.
     * @param inputStream   Input stream on which a request is available.
     * @param fetchBody     Boolean will determine if a body fetch is necessary.
     * @throws IOException  If something goes wrong during response reading.
     */
    public void fetchResponse(InputStream inputStream, boolean fetchBody) throws IOException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        fetchResponseHeader(httpInputStream);
        // Fetch the body if needed
        if (fetchBody) this.fetchBody(httpInputStream);
    }

    /**
     * Fetch the response header from the given http input stream.
     * @param httpInputStream   Input stream on which a response header is available.
     * @throws IOException      If something goes wrong during reading.
     */
    private void fetchResponseHeader(HTTPInputStream httpInputStream) throws IOException {
        String[] firstLine = httpInputStream.getNextLine().split(" ");
        String protocol = firstLine[0].trim();
        int status = Integer.parseInt(firstLine[1]);
        this.header = new HTTPResponseHeader(HTTPProtocol.parseProtocol(protocol), HTTPStatus.getStatusFor(status));
        httpInputStream.fillHeaderFields(this.header);
    }
}
