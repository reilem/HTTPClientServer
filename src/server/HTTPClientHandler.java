package server;

import common.HTTP.*;
import common.HTTP.exceptions.*;
import common.HTTP.header.HTTPRequestHeader;
import common.HTTP.header.HTTPResponseHeader;
import common.HTTP.message.HTTPRequest;
import common.HTTP.message.HTTPResponse;

import java.io.*;
import java.net.Socket;

/**
 * Class used by HTTPPooledServer to handle clients individually in a separate thread.
 */
public class HTTPClientHandler implements Runnable {

    // Server resource directory
    private static final String SERVER_DIR = "res-server";
    // The current client socket
    private final Socket client;

    /**
     * Constructor of a HTTPClientHandler
     * @param client Socket the client is running on.
     */
    HTTPClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        // Notify user
        System.out.println(Thread.currentThread().getName() + ": handling client.");
        // Handle the client
        this.handleClient();
    }

    /**
     * Handle the current client.
     */
    private void handleClient() {
        // Initialize a connection alive boolean which will determine if the handler will continue to try to receive
        // more requests from this client.
        boolean connectionAlive = true;
        // While connection with client is alive
        while (connectionAlive) {
            // Init a response header and response body.
            HTTPResponseHeader responseHeader;
            HTTPBody responseBody = null;
            // Default server protocol is 1.1 unless specified in the request header. Only 1.0 is also supported.
            HTTPProtocol protocol = HTTPProtocol.HTTP_1_1;
            try {
                // Make a request
                HTTPRequest request = new HTTPRequest();
                // Fetch request data available on client input stream
                request.fetchRequest(client.getInputStream());
                // Print the fetched header
                request.printHeader();
                // Get request header and body from the request
                HTTPRequestHeader requestHeader = request.getHeader();
                HTTPBody requestBody = request.getBody();
                // Get method from the request header
                HTTPMethod method = requestHeader.getMethod();

                // Check if method is supported
                if (!supportedMethod(method)) {
                    // If not supported throw method not implemented exception
                    throw new MethodNotImplementedException();
                } else if (method.equals(HTTPMethod.BREW)) {
                    // If BREW easter egg, throw tea pot exception
                    throw new CannotBrewCoffeeException();
                }
                // Check if protocol is supported
                if (!supportedProtocol(requestHeader.getProtocol())) {
                    // If not throw protocol not implemented exception
                    throw new ProtocolNotImplementedException();
                }
                // Get protocol from the request header.
                protocol = requestHeader.getProtocol();

                // Check if connection should be kept alive
                connectionAlive = requestHeader.keepConnectionAlive();
                // Check if valid header for this protocol (Host header included if 1.1)
                if (protocol.equals(HTTPProtocol.HTTP_1_1) && requestHeader.getFieldValue(HTTPField.HOST) == null) {
                    throw new InvalidHeaderException();
                }

                // Create a response header with status code 200
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_200);
                // Get valid server file path from request header path
                String serverFilePath = this.makeServerFilePath(HTTPUtil.makeFilePathFromPath(requestHeader.getPath()));
                // If file data is required from this method (GET or HEAD)
                if (method.requiresFileData()) {
                    // Fetch file data from server file path
                    FileData fileData = this.readFileDataFromPath(serverFilePath);
                    // Check for If-Modified-Since in header
                    HTTPTime checkModifyTime = (HTTPTime)requestHeader.getFieldValue(HTTPField.IF_MODIFIED_SINCE);
                    // If file not modified since given date
                    if (checkModifyTime != null && fileData.lastModified.time.isBefore(checkModifyTime.time)) {
                        // Turn response header into a 304 header
                        responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_304);
                        // Include file last modified date in response
                        responseHeader.addField(HTTPField.LAST_MODIFIED, fileData.lastModified);
                    } else if (method.equals(HTTPMethod.GET)) {
                        // If file modified and method is GET, pu file data in response body.
                        responseBody = new HTTPBody(fileData.data);
                        // Include content type and content length in response header
                        responseHeader.addField(HTTPField.CONTENT_TYPE, fileData.contentType);
                        responseHeader.addField(HTTPField.CONTENT_LENGTH, fileData.contentLength);
                    }
                } else if (method.requiresBody()) {
                    // If attempt to put or post onto index.html, throw access forbidden exception.
                    if (serverFilePath.equals(SERVER_DIR+"/index.html")) throw new AccessForbiddenException();
                    // If method is PUT, overwrite body to file data
                    if (method.equals(HTTPMethod.PUT)) requestBody.writeDataToFile(serverFilePath, false);
                    // If method is POST, append body to file data
                    else if (method.equals(HTTPMethod.POST)) requestBody.writeDataToFile(serverFilePath, true);
                }
            } catch (FileNotFoundException e) {
                // If file not found, make 404 response header
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_404);
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | InvalidHeaderException e) {
                // If exception indicates bad request header, make 400 response header
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_400);
            } catch (NullPointerException | IOException e) {
                // If exception indicates internal server error, make 500 response header
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_500);
            } catch (ContentLengthRequiredException e) {
                // If content length required, make 411 response header
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_411);
                // Reset the input stream
                try { client.getInputStream().reset(); } catch (IOException ignored) {}
                // connectionAlive = false;
            } catch (AccessForbiddenException e) {
                // If access forbidden, make 403 response header
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_403);
            } catch (MethodNotImplementedException e) {
                // If method not implemented, make 501 response header
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_501);
            } catch (ProtocolNotImplementedException e) {
                // If protocol not implemented, make 505 response header
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_505);
            } catch (CannotBrewCoffeeException e) {
                // If this server is asked to brew coffee, make 418 response header
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_418);
            } catch (NoContentFoundException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_204);
            } catch (TimeOutException e) {
                // Break out of the loop
                System.out.println(Thread.currentThread().getName() + " timed out.");
                break;
            }
            // Add extra header data such as date and connection state.
            responseHeader.addField(HTTPField.DATE, HTTPTime.getCurrentTime());
            responseHeader.addField(HTTPField.CONNECTION, Connection.parseConnection(connectionAlive));
            // Create response
            HTTPResponse response = new HTTPResponse(responseHeader, responseBody);
            // Try to send the response 5 times.
            int tries = 0;
            boolean responseSent = false;
            while (!responseSent && tries < 5) {
                try {
                    response.send(client.getOutputStream());
                    responseSent = true;
                } catch (IOException e) {
                    System.out.println("Internal error occurred when sending response.");
                    if (tries++ < 5) System.out.println("Trying again.");
                    else System.out.println("To many attempts, response failed.");
                }
            }
        }
        System.out.println("Connection closed.");
    }

    /**
     * Reads data from given file path on server.
     * @param serverFilePath    File path to file on server.
     * @return                  FileData found at file path.
     * @throws IOException      If something goes wrong during file reading.
     */
    private FileData readFileDataFromPath(String serverFilePath) throws IOException {
        // Get the file from path
        File f = this.getFileFromPath(serverFilePath);
        // Make a file reader to read from file
        FileReader fileReader = new FileReader(f);
        // Store data in byte array output stream
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        // While next byte is valid write it to output stream
        int next;
        while ((next = fileReader.read()) != -1) {
            data.write(next);
        }
        // Close file reader
        fileReader.close();
        // Calculate last modified date
        HTTPTime lastModified = new HTTPTime(f.lastModified());
        // Return the data in a FileData object
        return new FileData(serverFilePath, data.toByteArray(), lastModified);
    }

    /**
     * Get the file from given path.
     * @param serverFilePath            Valid path to file on server.
     * @return                          File found at file path.
     * @throws FileNotFoundException    If file not found or is a directory.
     */
    private File getFileFromPath(String serverFilePath) throws FileNotFoundException {
        // Find the file and check if it exists
        File f = new File(serverFilePath);
        if (!f.exists() || f.isDirectory()) throw new FileNotFoundException("File not found or is a directory " + serverFilePath);
        return f;
    }

    /**
     * Make valid path to file on server based on given relative path.
     * @param path                      Given relative path.
     * @return                          Valid path to file in the server directory.
     */
    private String makeServerFilePath(String path) {
        return SERVER_DIR + HTTPUtil.makeFilePathFromPath(path);
    }

    /**
     * Check if given method is supported.
     * @param m Given HTTP Method.
     * @return  True if GET, PUT, POST or HEAD. (BREW easter egg also supported).
     */
    private boolean supportedMethod(HTTPMethod m) {
        return m.equals(HTTPMethod.GET) || m.equals(HTTPMethod.PUT) || m.equals(HTTPMethod.POST) || m.equals(HTTPMethod.HEAD) || m.equals(HTTPMethod.BREW);
    }

    /**
     * Check if given protocol version is supported.
     * @param p Given HTTP Protocol.
     * @return  True if 1.1 or 1.0.
     */
    private boolean supportedProtocol(HTTPProtocol p) {
        return p.equals(HTTPProtocol.HTTP_1_1) || p.equals(HTTPProtocol.HTTP_1_0);
    }

    /**
     * Class to store essential File information and Data.
     */
    private class FileData {
        // Length of file data in bytes
        final int contentLength;
        // Content type of the file data
        final ContentType contentType;
        // Byte array storing data of the file
        final byte[] data;
        // HTTPTime object storing last modified time of file data.
        final HTTPTime lastModified;

        /**
         * Constructor of File Data.
         */
        FileData(String filePath, byte[] data, HTTPTime lastModified) throws IOException {
            this.contentLength = data.length;
            this.data = data;
            this.contentType = ContentType.parseContentTypeFromFile(filePath);
            this.lastModified = lastModified;
        }
    }

}
