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
            switch (requestHeader.getMethod()) {
                case GET:
                    FileData fileData = this.readFileDataFromPath(requestHeader.getPath());
                    responseBody = new HTTPBody(fileData.data);
                    responseHeader.addField(HTTPField.CONTENT_TYPE, fileData.contentType);
                    break;
            }
        } catch (IOException e) {
            responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_500);
        } catch (IllegalArgumentException e) {
            responseHeader = new HTTPResponseHeader(protocol, HTTPStatus.CODE_400);
        }

        HTTPResponse response = new HTTPResponse(responseHeader, responseBody);
        try {
            response.sendResponse(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendFileDataToPath(String path, byte[] body) throws IOException {
        // Get the correct resource path
        String resourcePath = makeServerFilePath(path);
        // Overwrite or create new file at path with body data
        Files.write(Paths.get(resourcePath), body, StandardOpenOption.APPEND);
    }

    private void overwriteFileDataToPath(String path, byte[] body) throws IOException {
        // Get the correct resource path
        String resourcePath = makeServerFilePath(path);
        // Overwrite or create new file at path with body data
        Files.write(Paths.get(resourcePath), body);
    }

    private FileData readFileDataFromPath(String path) throws IOException {
        // Get the file
        File f = this.getFileFromPath(path);
        // Read the data from the file
        FileReader fileReader = new FileReader(f);
        ByteArrayOutputStream fileData = new ByteArrayOutputStream();
        int next;
        while ((next = fileReader.read()) != -1) {
            fileData.write(next);
            fileData.write(HTTPUtil.CRLF.getBytes());
        }
        fileReader.close();
        // Return the file data
        return new FileData(path, fileData.toByteArray());
    }

    private File getFileFromPath(String path) throws FileNotFoundException {
        // Make relative file path from given path (in case it is in absolute form)
        String resourcePath = makeServerFilePath(path);
        // Find the file and check if it exists
        File f = new File(resourcePath);
        if (!f.exists() || f.isDirectory()) throw new FileNotFoundException("File not found or is a directory " + path);
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
        final long contentLength;
        final ContentType contentType;
        final byte[] data;

        FileData(String filePath, byte[] data) throws IOException {
            this.contentLength = data.length;
            this.data = data;
            this.contentType = ContentType.parseContentTypeFromFile(filePath);
        }


    }

}
