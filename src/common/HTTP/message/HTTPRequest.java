package common.HTTP.message;

import common.HTTP.*;
import common.HTTP.exceptions.ContentLengthRequiredException;
import common.HTTP.exceptions.InvalidHeaderException;
import common.HTTP.exceptions.TimeOutException;
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

    @Override
    public void printHeader() {
        // Print the current thread name and the header
        System.out.println("Request received.");
        System.out.println(this.header.toString());
    }

    /**
     * Fetch the request available on the given input stream.
     * @param inputStream                       Input stream on which a request is available.
     * @throws IOException                      If something goes wrong during request reading.
     * @throws ContentLengthRequiredException   If content length is required and none is given.
     */
    public void fetchRequest(InputStream inputStream) throws IOException, ContentLengthRequiredException, InvalidHeaderException, TimeOutException {
        // Create a http input stream
        HTTPInputStream httpInputStream = new HTTPInputStream(inputStream);
        // Fetch the header from the input stream
        fetchRequestHeader(httpInputStream);
        // If request method requires a body, attempt to fetch the body.
        if (this.header.getMethod().requiresBody()) this.fetchBody(httpInputStream);
    }

    /**
     * Fetch the request header from the given http input stream.
     * @param httpInputStream   Input stream on which a request header is available.
     * @throws IOException      If something goes wrong during reading.
     */
    private void fetchRequestHeader(HTTPInputStream httpInputStream) throws IOException, InvalidHeaderException, TimeOutException {
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
