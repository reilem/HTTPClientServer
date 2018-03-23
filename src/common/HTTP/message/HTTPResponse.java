package common.HTTP.message;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPStatus;
import common.HTTP.exceptions.ContentLengthRequiredException;
import common.HTTP.exceptions.TimeOutException;
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

    @Override
    public void printHeader() {
        // Print the current thread name and the header
        System.out.println("Response received.");
        System.out.println(this.header.toString());
    }

    /**
     * Fetch the response available on the given input stream.
     * @param inputStream                       Input stream on which a request is available.
     * @param fetchBody                         Boolean will determine if a body fetch is necessary.
     * @throws IOException                      If something goes wrong during response reading.
     */
    public void fetchResponse(InputStream inputStream, boolean fetchBody) throws IOException, TimeOutException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        fetchResponseHeader(httpInputStream);
        // Try to fetch the body if needed
        if (fetchBody) {
            try { this.fetchBody(httpInputStream); } catch (ContentLengthRequiredException ignore) {}
        }
    }

    /**
     * Fetch the response header from the given http input stream.
     * @param httpInputStream   Input stream on which a response header is available.
     * @throws IOException      If something goes wrong during reading.
     */
    private void fetchResponseHeader(HTTPInputStream httpInputStream) throws IOException, TimeOutException {
        // Get next line on the stream and split it on spaces
        String[] firstLine = httpInputStream.getNextLine().split(" ");
        // Get the protocol and status values from the line
        String protocol = firstLine[0].trim();
        int status = Integer.parseInt(firstLine[1]);
        // Parse the protocol and status values into a new HTTP Response Header
        this.header = new HTTPResponseHeader(HTTPProtocol.parseProtocol(protocol), HTTPStatus.parseStatus(status));
        // Fill in extra header fields.
        httpInputStream.fillHeaderFields(this.header);
    }
}
