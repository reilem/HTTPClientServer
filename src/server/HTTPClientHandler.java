package server;

import common.HTTP.*;
import common.HTTP.exceptions.*;
import common.HTTP.header.HTTPRequestHeader;
import common.HTTP.header.HTTPResponseHeader;
import common.HTTP.message.HTTPRequest;
import common.HTTP.message.HTTPResponse;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class HTTPClientHandler implements Runnable {

    // Server resource directory
    private static final String SERVER_DIR = "res-server";
    // The current client socket
    private final Socket client;
    // The current thread name
    private String threadName;

    /**
     * Constructor of a HTTPClientHandler
     * @param client the client socket
     */
    HTTPClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        this.threadName = Thread.currentThread().getName();
        System.out.println(this.threadName + ": handling client.");
        try {
            this.handleClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient() throws IOException {
        boolean connectionAlive = true;
        while (connectionAlive) {
            HTTPResponseHeader responseHeader;
            HTTPBody responseBody = null;
            HTTPProtocol protocol = HTTPProtocol.HTTP_1_1;
            try {
                HTTPRequest request = new HTTPRequest();
                request.fetchRequest(client.getInputStream());
                request.printHeader();
                HTTPRequestHeader requestHeader = request.getHeader();
                HTTPBody requestBody = request.getBody();
                HTTPMethod method = requestHeader.getMethod();

                if (!supportedMethod(method)) {
                    throw new MethodNotImplementedException();
                } else if (method.equals(HTTPMethod.BREW)) {
                    throw new TeaPotException();
                }
                if (!supportedProtocol(requestHeader.getProtocol())) {
                    throw new ProtocolNotImplementedException();
                }

                protocol = requestHeader.getProtocol();

                // Check if connection should be kept alive
                connectionAlive = requestHeader.keepConnectionAlive();
                // Check if valid header for this protocol (Host header included if 1.1)
                if (protocol.equals(HTTPProtocol.HTTP_1_1) && requestHeader.getFieldValue(HTTPField.HOST) == null) {
                    throw new InvalidHeaderException();
                }

                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_200);
                String serverFilePath = this.makeServerFilePath(HTTPUtil.makeFilePathFromPath(requestHeader.getPath()));
                if (method.requiresFileData()) {
                    // Fetch file data for HEAD or GET
                    FileData fileData = this.readFileDataFromPath(serverFilePath);
                    HTTPTime checkModifyTime = (HTTPTime)requestHeader.getFieldValue(HTTPField.IF_MODIFIED_SINCE);
                    if (checkModifyTime != null && fileData.lastModified.time.isBefore(checkModifyTime.time)) {
                        responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_304);
                        responseHeader.addField(HTTPField.LAST_MODIFIED, fileData.lastModified);
                    } else if (method.equals(HTTPMethod.GET)) {
                        responseBody = new HTTPBody(fileData.data);
                        responseHeader.addField(HTTPField.CONTENT_TYPE, fileData.contentType);
                        responseHeader.addField(HTTPField.CONTENT_LENGTH, fileData.contentLength);
                    }
                } else if (method.requiresBody()) {
                    // Write data for PUT or POST
                    if (serverFilePath.equals(SERVER_DIR+"/index.html")) throw new AccessForbiddenException();
                    if (method.equals(HTTPMethod.PUT)) overwriteFileDataToPath(serverFilePath, requestBody.getData());
                    else if (method.equals(HTTPMethod.POST)) appendFileDataToPath(serverFilePath, requestBody.getData());
                }
            } catch (FileNotFoundException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_404);
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException | InvalidHeaderException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_400);
            } catch (NullPointerException | IOException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_500);
            } catch (ContentLengthRequiredException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_411);
                connectionAlive = false;
            } catch (AccessForbiddenException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_403);
            } catch (MethodNotImplementedException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_501);
            } catch (ProtocolNotImplementedException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_505);
            } catch (TeaPotException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_418);
            }

            // Add extra header data
            responseHeader.addField(HTTPField.DATE, HTTPTime.getCurrentTime());
            responseHeader.addField(HTTPField.CONNECTION, Connection.parseConnection(connectionAlive));
            // Send response
            HTTPResponse response = new HTTPResponse(responseHeader, responseBody);
            response.send(client.getOutputStream());
        }
        System.out.println("Connection closed.");
    }

    private void appendFileDataToPath(String serverFilePath, byte[] body) throws IOException {
        // Overwrite or create new file at path with body data
        Files.write(Paths.get(serverFilePath), body, StandardOpenOption.APPEND);
    }

    private void overwriteFileDataToPath(String serverFilePath, byte[] body) throws IOException {
        // Overwrite or create new file at path with body data
        Files.write(Paths.get(serverFilePath), body);
    }

    private FileData readFileDataFromPath(String serverFilePath) throws IOException {
        // Get the file
        File f = this.getFileFromPath(serverFilePath);
        // Read the data from the file
        FileReader fileReader = new FileReader(f);
        ByteArrayOutputStream fileData = new ByteArrayOutputStream();
        int next;
        while ((next = fileReader.read()) != -1) {
            fileData.write(next);
        }
        fileReader.close();
        // Calculate last modified date
        HTTPTime lastModified = new HTTPTime(f.lastModified());
        // Return the file data
        return new FileData(serverFilePath, fileData.toByteArray(), lastModified);
    }

    private File getFileFromPath(String serverFilePath) throws FileNotFoundException {
        // Find the file and check if it exists
        File f = new File(serverFilePath);
        if (!f.exists() || f.isDirectory()) throw new FileNotFoundException("File not found or is a directory " + serverFilePath);
        return f;
    }

    private String makeServerFilePath(String path) throws IllegalArgumentException{
        return SERVER_DIR + HTTPUtil.makeFilePathFromPath(path);
    }

    private boolean supportedMethod(HTTPMethod m) {
        return m.equals(HTTPMethod.GET) || m.equals(HTTPMethod.PUT) || m.equals(HTTPMethod.POST) || m.equals(HTTPMethod.HEAD) || m.equals(HTTPMethod.BREW);
    }

    private boolean supportedProtocol(HTTPProtocol p) {
        return p.equals(HTTPProtocol.HTTP_1_1) || p.equals(HTTPProtocol.HTTP_1_0);
    }

    private class FileData {
        final int contentLength;
        final ContentType contentType;
        final byte[] data;
        final HTTPTime lastModified;

        FileData(String filePath, byte[] data, HTTPTime lastModified) throws IOException {
            this.contentLength = data.length;
            this.data = data;
            this.contentType = ContentType.parseContentTypeFromFile(filePath);
            this.lastModified = lastModified;
        }


    }

}
