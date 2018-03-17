package client;

import common.HTML.HTMLUtil;
import common.HTTP.header.HTTPRequestHeader;
import common.HTTP.header.HTTPResponseHeader;
import common.HTTP.message.HTTPRequest;
import common.HTTP.message.HTTPResponse;
import common.HTTP.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for a HTTP client.
 */
public class HTTPClient {

    // The name of the directory containing all client side data
    private static final String CLIENT_DIR = "res-client";

    // The client's socket
    private final Socket httpSocket;

    /**
     * Constructor for a HTTP Client.
     * @param port          Port number to which the socket will try to connect.
     * @param uri           Uri containing a host name to which the socket will try to connect.
     * @throws IOException  If the given host or port cannot be connected to.
     */
    public HTTPClient(int port, URI uri) throws IOException {
        // Create socket based on internet address of host name provided by uri and port
        this.httpSocket = new Socket(InetAddress.getByName(uri.getHost()), port);
    }

    /**
     * Executes a HTTP request with given parameters.
     * @param method              HTTP method of the request.
     * @param uri                 Uri of the request.
     * @param protocol            HTTP protocol to be used in the request.
     * @param body                Body of the request, must include CRLF characters as newlines.
     * @param extraHeader         HashMap of extra content that will be put into the request header.
     * @throws IOException        If something goes wrong during IO.
     * @throws URISyntaxException If something goes wrong during URI creation (for redirects and image requests).
     */
    public void executeRequest(HTTPMethod method, URI uri, HTTPProtocol protocol, HTTPBody body, HashMap<HTTPField, Object> extraHeader) throws IOException, URISyntaxException {
        // If socket is null or is closed, print a warning message and return
        if (this.httpSocket == null || this.httpSocket.isClosed()) { System.out.println("Socket is closed."); return; }

        // Create request header with given parameters
        HTTPRequestHeader requestHeader = new HTTPRequestHeader(method, uri.getPath(), protocol);
        // If a Host header field is required, place it in the request
        if (protocol.equals(HTTPProtocol.HTTP_1_1)) requestHeader.addField(HTTPField.HOST, uri.getHost());
        // Determine the request body
        HTTPBody requestBody = null;
        if (requestHeader.getMethod().requiresBody()) {
            // If given body is null, but one is needed, request user input. Else use the given body
            if (body == null) requestBody = getUserInput();
            else requestBody = body;
            // Add body content type and content length to header
            requestHeader.addField(HTTPField.CONTENT_LENGTH, requestBody.getData().length);
            requestHeader.addField(HTTPField.CONTENT_TYPE, new ContentType("text", "plain", "utf-8"));
        }
        // Add any extra header
        if (extraHeader != null) {
            extraHeader.forEach(requestHeader::addField);
        }
        // Create request object and send the request
        HTTPRequest request = new HTTPRequest(requestHeader, requestBody);
        request.send(this.httpSocket.getOutputStream());
        // Notify the user
        System.out.println("Request sent...");
        // Create response object and fetch the response data
        HTTPResponse response = new HTTPResponse();
        response.fetchResponse(this.httpSocket.getInputStream(), !requestHeader.getMethod().equals(HTTPMethod.HEAD));
        // Get the response header & body from response object
        HTTPResponseHeader responseHeader = response.getHeader();
        HTTPBody responseBody = response.getBody();
        // Determine if connection is being kept alive from the header
        boolean keepAlive = responseHeader.keepConnectionAlive();
        if (!keepAlive) {
            this.httpSocket.close();
        }
        // Print out the header data
        System.out.println("Response received."+ HTTPUtil.NEW_LINE+responseHeader.toString());
        // Get the status code of the response
        HTTPStatus status = responseHeader.getStatus();
        // Check if redirection is needed
        if (requestHeader.getMethod().equals(HTTPMethod.GET) && status.equals(HTTPStatus.CODE_302)) {
            // Get the location of the redirection from the header
            String location = (String)responseHeader.getFieldValue(HTTPField.LOCATION);
            // Print the response body if it isn't null
            if (responseBody != null) responseBody.printData(null);
            // Notify user that a redirect is being followed
            System.out.println("Following redirect...");
            // Execute a request to the new location
            this.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI(location), protocol, null, null);
        } else if (responseBody != null) {
            // Check the content type, extension and charSet of the response
            ContentType contentType = (ContentType)responseHeader.getFieldValue(HTTPField.CONTENT_TYPE);
            String charSet = null;
            String type = null;
            String ext = null;
            if (contentType != null) {
                charSet = contentType.getCharSet();
                type = contentType.getType();
                ext = contentType.getExtension();
            }
            // Print data of the response with given charset
            responseBody.printData(charSet);
            // Check if type of content was text
            if (type != null && type.equals("text") && !status.equals(HTTPStatus.CODE_404)) {
                // Get responseText as string from responseBody with correct charSet
                String responseText = responseBody.getAsString(charSet);
                // Check if file extension is html
                if (ext != null && ext.equals("html") && keepAlive) {
                    // Parse the sources from image tags
                    ArrayList<String> extraPaths = HTMLUtil.getImageURLs(responseText);
                    // Make requests for the image sources
                    for (String imagePath : extraPaths) {
                        System.out.println("Requesting images...");
                        this.executeRequest(HTTPMethod.GET, HTTPUtil.makeURI(uri.getHost()+"/"+imagePath), protocol, null, null);
                    }
                    // To make sure the image sources are properly found, make sure no sources start with a slash.
                    responseText = responseText.replaceAll("src=\"/", "src=\"");
                }
                // Make new response body from response text
                responseBody = new HTTPBody(responseText);
            }
            // Write response body to file
            responseBody.writeDataToFile(makeClientFilePath(uri));
        }
    }

    /**
     * Makes a valid path String to the client side data directory based on given uri.
     * @param uri   Given uri.
     * @return      Valid path String to the client side data directory.
     */
    private String makeClientFilePath(URI uri) {
        return CLIENT_DIR + HTTPUtil.makeFilePathFromURI(uri);
    }

    /**
     * Asks user for input and returns in the input as a HTTPBody object.
     * @return              HTTPBody object containing the user's input.
     * @throws IOException  If something goes wrong during IO.
     */
    private HTTPBody getUserInput() throws IOException {
        System.out.println("Please input request body:");
        BufferedReader inputRead = new BufferedReader(new InputStreamReader(System.in));
        String nextInput;
        StringBuilder input = new StringBuilder();
        while (!(nextInput = inputRead.readLine()).equals("")) {
            input.append(nextInput);
            input.append(HTTPUtil.CRLF);
        }
        return new HTTPBody(input.toString().getBytes());
    }
}
