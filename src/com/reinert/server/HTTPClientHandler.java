package com.reinert.server;

import com.reinert.common.HTTP.*;
import com.reinert.common.HTTP.exceptions.AccessForbiddenException;
import com.reinert.common.HTTP.exceptions.ContentLengthRequiredException;
import com.reinert.common.HTTP.exceptions.InvalidHeaderException;
import com.reinert.common.HTTP.exceptions.MethodNotImplementedException;
import com.reinert.common.HTTP.header.HTTPRequestHeader;
import com.reinert.common.HTTP.header.HTTPResponseHeader;
import com.reinert.common.HTTP.message.HTTPRequest;
import com.reinert.common.HTTP.message.HTTPResponse;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.reinert.common.HTTP.HTTPMethod.*;

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
        this.handleClient();
    }

    private void handleClient() {
        boolean connectionAlive = true;
        while (connectionAlive) {
            HTTPResponseHeader responseHeader;
            HTTPBody responseBody = null;
            HTTPProtocol protocol = HTTPProtocol.HTTP_1_1;
            try {
                HTTPRequest request = new HTTPRequest();
                request.fetchRequest(client.getInputStream());
                HTTPRequestHeader requestHeader = request.getHeader();
                HTTPBody requestBody = request.getBody();
                protocol = requestHeader.getProtocol();

                System.out.println(this.threadName + ": request received.");
                System.out.println(requestHeader.toString());

                // Check if connection should be kept alive
                connectionAlive = requestHeader.keepConnectionAlive();
                // Check if valid header for this protocol (Host header included if 1.1)
                if (protocol.equals(HTTPProtocol.HTTP_1_1) && requestHeader.getFieldValue(HTTPField.HOST) == null) {
                    throw new InvalidHeaderException();
                }

                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_200);
                String serverFilePath = this.makeServerFilePath(HTTPUtil.makeFilePathFromPath(requestHeader.getPath()));
                HTTPMethod method = requestHeader.getMethod();
                if (method.requiresFileData()) {
                    // Fetch file data for HEAD or GET
                    FileData fileData = this.readFileDataFromPath(serverFilePath);
                    HTTPTime checkModifyTime = (HTTPTime) requestHeader.getFieldValue(HTTPField.IF_MODIFIED_SINCE);
                    if (fileData.lastModified.time.isAfter(checkModifyTime.time)) {
                        responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_304);
                        responseHeader.addField(HTTPField.LAST_MODIFIED, fileData.lastModified);
                    } else if (method.equals(GET)) {
                        responseBody = new HTTPBody(fileData.data);
                    }
                    responseHeader.addField(HTTPField.CONTENT_TYPE, fileData.contentType);
                    responseHeader.addField(HTTPField.CONTENT_LENGTH, fileData.contentLength);
                } else if (method.requiresBody()) {
                    // Write data for PUT or POST
                    if (serverFilePath.equals(SERVER_DIR+"/index.html")) throw new AccessForbiddenException();
                    if (method.equals(PUT)) overwriteFileDataToPath(serverFilePath, requestBody.getData());
                    else if (method.equals(POST)) appendFileDataToPath(serverFilePath, requestBody.getData());
                }
            } catch (FileNotFoundException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_404);
            } catch (IllegalArgumentException | InvalidHeaderException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_400);
            } catch (NullPointerException | IOException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_500);
            } catch (ContentLengthRequiredException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_411);
            } catch (AccessForbiddenException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_403);
            } catch (MethodNotImplementedException e) {
                responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_501);
            }

            // Add extra header data
            responseHeader.addField(HTTPField.DATE, HTTPTime.getCurrentTime());
            // Send response
            HTTPResponse response = new HTTPResponse(responseHeader, responseBody);
            try {
                response.sendResponse(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
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
