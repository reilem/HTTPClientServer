package common.HTTP.message;

import common.HTTP.*;
import common.HTTP.exceptions.ContentLengthRequiredException;
import common.HTTP.exceptions.InvalidHeaderException;
import common.HTTP.header.HTTPRequestHeader;

import java.io.*;

/**
 * A class for HTTPRequests. Includes methods for sending and fetching requests.
 */
public class HTTPRequest extends HTTPMessage {

    // The header of the current request
    private HTTPRequestHeader header;

    /**
     * Constructor for a HTTPRequest requiring no parameters
     */
    public HTTPRequest() {}

    /**
     * Constructor for a HTTPRequest.
     * @param header    Request header of the current request.
     * @param body      Body of the current request.
     */
    public HTTPRequest(HTTPRequestHeader header, HTTPBody body) {
        super(body);
        this.header = header;
    }

    @Override
    public HTTPRequestHeader getHeader() { return header; }

    /**
     * Fetch the request available on the given input stream.
     * @param inputStream                       Input stream on which a request is available.
     * @throws IOException                      If something goes wrong during request reading.
     * @throws ContentLengthRequiredException   If content length is required and none if given.
     */
    public void fetchRequest(InputStream inputStream) throws IOException, ContentLengthRequiredException, InvalidHeaderException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        fetchRequestHeader(httpInputStream);
        // Determine method & protocol
        HTTPMethod method =  this.header.getMethod();
        HTTPProtocol protocol = this.header.getProtocol();
        // Determine content length and transfer encoding
        Object length = this.header.getFieldValue(HTTPField.CONTENT_LENGTH);
        Object encoding = this.header.getFieldValue(HTTPField.TRANSFER_ENCODING);
        // If request method requires a body, no content length is given and chunked transfer encoding is not
        // specified or not valid for this protocol then throw a content length required exception.
        if (method.requiresBody() && length == null && (encoding == null || !encoding.equals(HTTPUtil.CHUNKED) || !protocol.equals(HTTPProtocol.HTTP_1_1))) {
            printRequestHeader();
            throw new ContentLengthRequiredException();
        }
        // Fetch the body from the input stream
        this.fetchBody(httpInputStream);
    }

    /**
     * Prints the current header.
     */
    public void printRequestHeader() {
        // Print the current thread name and the header
        System.out.println(Thread.currentThread().getName() + ": request received.");
        System.out.println(this.header.toString());
    }

    /**
     * Fetch the request header from the given http input stream.
     * @param httpInputStream   Input stream on which a request header is available.
     * @throws IOException      If something goes wrong during reading.
     */
    private void fetchRequestHeader(HTTPInputStream httpInputStream) throws IOException, InvalidHeaderException {
        // Get the first line on the input stream
        String[] firstLine = httpInputStream.getNextLine().split(" ");
        // If first line does not contain at least two elements throw invalid header exception
        if (firstLine.length < 2) throw new InvalidHeaderException();
        // Parse method, path and protocol from first line
        String method = firstLine[0];
        String path = firstLine[1];
        String protocol = "";
        if (firstLine.length >= 3 && firstLine[2] != null) protocol = firstLine[2].trim();
        // Attempt to parse the method and protocol strings and create a new HTTPRequestHeader
        this.header = new HTTPRequestHeader(HTTPMethod.parseMethod(method), path, HTTPProtocol.parseProtocol(protocol));
        // Fill the header with any remaining header fields from the input stream
        httpInputStream.fillHeaderFields(this.header);
    }
}
