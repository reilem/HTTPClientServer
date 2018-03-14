package com.reinert.server;

import com.reinert.common.HTTPUtil;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;

public class HTTPClientHandler implements Runnable {
    // Use DateTimeFormatter for better thread safety
    private static final DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
    // The current client socket
    private final Socket client;
    // The current thread name
    private String threadName;
    // Checks if connection should be kept alive
    private boolean keepAlive;
    // The current active protocol
    private String protocol;

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
        try {
            // Open a response and request reader
            BufferedReader requestReader = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            BufferedWriter responseWriter = new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream()));
            while (true) {
                // Read the request
                String request = this.readRequest(requestReader);
                if (!request.isEmpty()) {
                    String[] requestData = request.split(HTTPUtil.CRLF);
                    this.handleRequest(requestData, requestReader, responseWriter);
                    if (!this.keepAlive) {
                        requestReader.close();
                        responseWriter.close();
                        client.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(String[] httpRequest, BufferedReader requestReader, Writer responseWriter) throws IOException {
        System.out.println(this.threadName + " handling request: " + Arrays.toString(httpRequest));
        // Split into components
        String[] requestComponents = httpRequest[0].split(" ");
        // Get the request method
        String method = requestComponents[0];
        // Get the requested path
        String path = null;
        try {
            path = HTTPUtil.parsePath(requestComponents[1]);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // If path is a single forward slash set it to the index
        if (path.equals("/")) path = "/index.html";
        // Get the protocol
        this.protocol = requestComponents[2];

        // Check if the request has valid method
        boolean badRequest = !method.matches("GET|PUT|POST|HEAD");
        // Check protocol version
        boolean isHttp1_1 = this.protocol.equals("HTTP/1.1");
        if (!this.protocol.matches("HTTP/1.1|HTTP/1.0")) {
            this.protocol = "HTTP/1.1";
            badRequest = true;
        }
        // Iterate through all headers to find relevant information
        this.keepAlive = isHttp1_1;
        ZonedDateTime lastModified = null;
        String host = null;
        for (int i = 1; i < httpRequest.length; i++) {
            String requestLine = httpRequest[i];
            int index = requestLine.indexOf(':');
            if (index == -1) break;
            String header = requestLine.substring(0, index+1);
            String headerValue = requestLine.substring(index+2);
            switch (header) {
                case "Host:":
                    // Check that the header value only contains 1 word for host, else bad request
                    if (headerValue.split(" ").length == 1) host = headerValue;
                    else badRequest = true;
                    break;
                case "Connection:":
                    // Check if request provides a connection preference
                    if (headerValue.equals("keep-alive")) keepAlive = true;
                    else if (headerValue.equals("close")) keepAlive = false;
                    break;
                case "If-Modified-Since:":
                    // Check if request has a last modified value
                    lastModified = getTimeFor(headerValue);
                    break;
            }
        }
        // If bad request, or http/1.1 with no host, return status 400.
        if (badRequest || (isHttp1_1 && host == null)) {
            responseWriter.write(getResponseHeader(400, null, null));
            responseWriter.flush();
            return;
        }

        // Perform requested service
        try {
            String body;
            switch (method) {
                case "GET":
                    FileData fileData = readFileDataFromPath(path);
                    responseWriter.write(getResponseHeader(200, fileData.contentType, fileData.contentLength));
                    responseWriter.write(fileData.data);
                    break;
                case "PUT":
                    body = readRequest(requestReader);
                    this.overwriteFileDataToPath(path, body);
                    responseWriter.write(getResponseHeader(200, null, null));
                    break;
                case "POST":
                    body = readRequest(requestReader);
                    this.appendFileDataToPath(path, body);
                    responseWriter.write(getResponseHeader(200, null, null));
                    break;
                case "HEAD":
                    responseWriter.write(getResponseHeader(200, null, null));
                    break;
            }
        } catch (FileNotFoundException e) {
            responseWriter.write(getResponseHeader(404, null, null));
        }
        responseWriter.flush();
    }

    private String readRequest(BufferedReader reader) throws IOException {
        // Read the request
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null && !line.equals("")) {
            body.append(line);
            body.append(HTTPUtil.CRLF);
        }
        return body.toString();
    }

    private String getResponseHeader(int statusCode, String contentType, Long contentLength) {
        // Determine connection response type
        String connection;
        if (this.keepAlive) connection = "keep-alive";
        else connection = "close";

        String contentHead = "";
        if (contentType != null) {
            contentHead += ("Content-Type: " + contentType + HTTPUtil.CRLF);
        }
        if (contentLength != null) {
            contentHead += ("Content-Length: " + contentLength + HTTPUtil.CRLF);
        }
        // TODO: ADD LAST MODIFIED

        // Generate a generic response body
        String genericResponseBody = (
                "Date: " + getCurrentTime() + HTTPUtil.CRLF + contentHead +
                "Connection: " + connection + HTTPUtil.CRLF + HTTPUtil.CRLF
        );

        // TODO: add 411 length required
        // Determine the response based on status code
        switch (statusCode) {
            case 200:
                return (this.protocol + " 200 OK" + HTTPUtil.CRLF + genericResponseBody);
            case 404:
                return (this.protocol + " 404 Not Found" + HTTPUtil.CRLF + genericResponseBody);
            case 400:
                return (this.protocol + " 400 Bad Request" + HTTPUtil.CRLF + genericResponseBody);
            case 500:
                return (this.protocol + " 500 Internal Server Error" + HTTPUtil.CRLF + genericResponseBody);
            case 304:
                return (this.protocol + " 304 Not Modified" + HTTPUtil.CRLF + genericResponseBody);
            default:
                throw new IllegalArgumentException("Invalid status code");
        }
    }

    private void appendFileDataToPath(String path, String body) throws IOException {
        // Get the correct resource path
        String resourcePath = getResourcePathFrom(path);
        // Overwrite or create new file at path with body data
        Files.write(Paths.get(resourcePath), Collections.singleton(body), StandardOpenOption.APPEND);
    }

    private void overwriteFileDataToPath(String path, String body) throws IOException {
        // Get the correct resource path
        String resourcePath = getResourcePathFrom(path);
        // Overwrite or create new file at path with body data
        Files.write(Paths.get(resourcePath), body.getBytes());
    }

    private FileData readFileDataFromPath(String path) throws IOException {
        // Get the file
        File f = this.getFileFromPath(path);
        // Read the data from the file
        BufferedReader fileReader = new BufferedReader(new FileReader(f));
        StringBuilder fileData = new StringBuilder();
        String fileLine;
        while ((fileLine = fileReader.readLine()) != null) {
            fileData.append(fileLine);
            fileData.append(HTTPUtil.CRLF);
        }
        fileReader.close();
        // Return the file data
        return new FileData(path, fileData.toString());
    }

    private File getFileFromPath(String path) throws FileNotFoundException {
        // Make relative file path from given path (in case it is in absolute form)
        String resourcePath = getResourcePathFrom(path);
        // Find the file and check if it exists
        File f = new File(resourcePath);
        if (!f.exists() || f.isDirectory()) throw new FileNotFoundException("File not found or is a directory");
        return f;
    }

    private String getCurrentTime() {
        ZonedDateTime date = ZonedDateTime.now(ZoneId.of("GMT"));
        return date.format(formatter);
    }

    private ZonedDateTime getTimeFor(String timeString) {
        return ZonedDateTime.parse(timeString, formatter);
    }

    private String getResourcePathFrom(String path) throws IllegalArgumentException{
        int i = path.indexOf('/');
        if (i != -1) return ("resources/"+path.substring(i+1));
        else throw new IllegalArgumentException("Invalid path format");
    }

    private class FileData {
        final long contentLength;
        final String contentType;
        final String data;

        FileData(String filePath, String data) {
            this.contentLength = data.getBytes().length;
            this.data = data;
            this.contentType = this.getFileType(filePath);
        }

        private String getFileType(String path) {
            int index = path.lastIndexOf('.');
            if (index != -1) {
                String fileType = path.substring(index+1);
                switch (fileType) {
                    case "html":
                        return "text/html";
                    case "txt":
                        return "text";
                }
            }
            return null;
        }
    }

}
