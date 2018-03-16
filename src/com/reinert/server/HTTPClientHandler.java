package com.reinert.server;

import com.reinert.common.HTTP.*;
import com.reinert.common.HTTP.header.HTTPRequestHeader;
import com.reinert.common.HTTP.header.HTTPResponseHeader;
import com.reinert.common.HTTP.message.HTTPRequest;
import com.reinert.common.HTTP.message.HTTPResponse;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HTTPClientHandler implements Runnable {
    // Use DateTimeFormatter for better thread safety
    private static final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
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
        System.out.println("Thread started with client: " + this.threadName);
        this.handleClient();
    }

    private void handleClient() {
        HTTPResponseHeader responseHeader;
        HTTPBody responseBody = null;
        HTTPProtocol protocol = HTTPProtocol.HTTP_1_1;
        try {
            HTTPRequest request = new HTTPRequest();
            request.fetchRequest(client.getInputStream());
            HTTPRequestHeader requestHeader = request.getHeader();
            HTTPBody requestBody = request.getBody();

            protocol = requestHeader.getProtocol();
            responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_200);
            String serverFilePath = this.makeServerFilePath(HTTPUtil.makeFilePathFromPath(requestHeader.getPath()));
            FileData fileData;
            switch (requestHeader.getMethod()) {
                case GET:
                    fileData = this.readFileDataFromPath(serverFilePath);
                    responseBody = new HTTPBody(fileData.data);
                    responseHeader.addField(HTTPField.CONTENT_TYPE, fileData.contentType);
                    responseHeader.addField(HTTPField.CONTENT_LENGTH, fileData.contentLength);
                    break;
                case HEAD:
                    fileData = this.readFileDataFromPath(serverFilePath);
                    responseHeader.addField(HTTPField.CONTENT_TYPE, fileData.contentType);
                    responseHeader.addField(HTTPField.CONTENT_LENGTH, fileData.contentLength);
                    break;
                case PUT:
                    overwriteFileDataToPath(serverFilePath, requestBody.getData());
                    break;
                case POST:
                    appendFileDataToPath(serverFilePath, requestBody.getData());
                    break;
            }
        } catch (FileNotFoundException e) {
            responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_404);
        } catch (IllegalArgumentException e) {
            responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_400);
        } catch (IOException e) {
            responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_500);
        }

        HTTPResponse response = new HTTPResponse(responseHeader, responseBody);
        try {
            response.sendResponse(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        // Return the file data
        return new FileData(serverFilePath, fileData.toByteArray());
    }

    private File getFileFromPath(String serverFilePath) throws FileNotFoundException {
        // Find the file and check if it exists
        File f = new File(serverFilePath);
        if (!f.exists() || f.isDirectory()) throw new FileNotFoundException("File not found or is a directory " + serverFilePath);
        return f;
    }

    private String getCurrentTime() {
        ZonedDateTime date = ZonedDateTime.now(ZoneId.of("GMT"));
        return date.format(formatter);
    }

    private ZonedDateTime getTimeFor(String timeString) {
        return ZonedDateTime.parse(timeString, formatter);
    }

    private String makeServerFilePath(String path) throws IllegalArgumentException{
        return SERVER_DIR + HTTPUtil.makeFilePathFromPath(path);
    }

    private class FileData {
        final int contentLength;
        final ContentType contentType;
        final byte[] data;

        FileData(String filePath, byte[] data) throws IOException {
            this.contentLength = data.length;
            this.data = data;
            this.contentType = ContentType.parseContentTypeFromFile(filePath);
        }


    }

}
