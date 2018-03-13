package com.reinert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPClient {

    private final Socket httpSocket;

    HTTPClient(int port, String uriString) {
        // Create socket based on host name provided by uri
        Socket s = null;
        try {
            s = new Socket(InetAddress.getByName(HTTPUtil.parseHostName(uriString)), port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.httpSocket = s;
    }

    /**
     * Executes a HTTP request with given parameters.
     * @param method        the HTTP method of the request
     * @param requestURI    the uri of the request
     * @param protocol      the HTTP protocol to be used
     * @param body          the body of the request, must include CRLF character for newline
     * @return              a string containing the HTTP response
     */
    public void executeRequest(String method, String requestURI, String protocol, String body) {
        if (this.httpSocket == null) return;
        System.out.println("Executing request...");
        // Create a response string builder
        StringBuilder response = new StringBuilder();
        try {
            // Open request & response streams
            BufferedWriter requestOutput = new BufferedWriter(new OutputStreamWriter(httpSocket.getOutputStream()));
            InputStream responseInput = httpSocket.getInputStream();

            // Parse host name from given uri
            String hostName = HTTPUtil.parseHostName(requestURI);
            // Write the main request line to output stream
            requestOutput.write(getRequestLine(method, requestURI, protocol));
            // If HTTP/1.1 include a host header
            if (protocol.equals("HTTP/1.1")) requestOutput.write(getHostHeader(hostName));
            // If body is given, include it
            if (body != null && !body.isEmpty()) {
                requestOutput.write(HTTPUtil.CRLF);
                requestOutput.write(body);
                requestOutput.write(HTTPUtil.CRLF);
            }
            requestOutput.write(HTTPUtil.CRLF);
            requestOutput.flush();
            System.out.println("Request sent...");

            Boolean connectionOpen = null;
            Long contentLength = null;
            String contentType = "text";
            String charSet = "utf-8";
            StringBuilder line = new StringBuilder();
            int next = responseInput.read();
            boolean previousCR = false;
            while (next != -1) {
                char nextChar = (char)next;
                if (!previousCR && nextChar == HTTPUtil.CR) {
                    previousCR = true;
                } else if (previousCR && nextChar == HTTPUtil.LF) {
                    if (line.length() == 0) break;
                    int index = line.toString().indexOf(':');
                    if (index != -1) {
                        String tag = line.substring(0, index+1).toLowerCase();
                        String value = line.substring(index+2).toLowerCase();
                        switch (tag) {
                            case "content-type:":
                                int t = value.indexOf(';');
                                if (t == -1) contentType = value;
                                else {
                                    int l = value.indexOf('=');
                                    contentType = value.substring(0,t);
                                    charSet = value.substring(l+1).toUpperCase();
                                }
                                break;
                            case "content-length:":
                                contentLength = Long.parseLong(value);
                                break;
                            case "connection:":
                                connectionOpen = value.equals("keep-alive");
                                break;
                        }
                    }
                    response.append(line);
                    response.append(HTTPUtil.NEW_LINE);
                    line.setLength(0);
                    previousCR = false;
                } else {
                    line.append(nextChar);
                }
                next = responseInput.read();
            }
            response.append(HTTPUtil.NEW_LINE);
            System.out.println("Header received: " + HTTPUtil.NEW_LINE + response);
            response.setLength(0);

            // TODO use byte stream instead of buffers
            int bufferSize = 2048;
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(bufferSize);
            int val = responseInput.read(buffer);
            System.out.println(val);
            long length = 0;
            while (true) {
                byteOutput.write(buffer);
                length += val;
                if (contentLength != null && length >= contentLength) {
                    System.out.println(length);
                    break;
                }
                val = responseInput.read(buffer, 0, val);
                System.out.println(val);
            }


            // Parse the received byte array
            if (contentType.startsWith("text")) {
                // Convert byte output to string with given charSet
                String textData = byteOutput.toString(charSet);
                // Append byte output to response string
                response.append(textData);
                // Get index for secondary file type
                int index = contentType.indexOf('/');
                // Get file type
                String fileType = contentType.substring(index + 1);
                String params = HTTPUtil.parseParams(requestURI);
                String filePath = params.equals("/") ? "index.html" : params;
                // Get the correct resource path
                String resourcePath = "res-client/"+filePath;
                // Overwrite or create new file at path with byteOutput data
                Files.write(Paths.get(resourcePath), byteOutput.toByteArray());
                // Find any <img /> tags if html
                if (fileType.equals("html")) {
                    // Lower cases makes pattern matching easier
                    String htmlData = textData.toLowerCase();
                    // Make an image tag pattern
                    Pattern imgTagPattern = Pattern.compile("<img src=.*(>|/>)");
                    // Make quotes pattern
                    Pattern quotePattern = Pattern.compile("\".*\"");
                    // Make matcher for image tags with html data
                    Matcher imgTagMatcher = imgTagPattern.matcher(htmlData);
                    // Make a list to store found image paths
                    ArrayList<String> imagePaths = new ArrayList<>();
                    // While img tag matcher finds matched
                    while (imgTagMatcher.find()) {
                        // Get the image tag sub string
                        String imgTag = htmlData.substring(imgTagMatcher.start(), imgTagMatcher.end());
                        // Make a matcher to find quotes in the image tag
                        Matcher quoteMatcher = quotePattern.matcher(imgTag);
                        // If quotes found
                        if (quoteMatcher.find()) {
                            // Extract the file path from the image tag
                            imagePaths.add(imgTag.substring(quoteMatcher.start()+1, quoteMatcher.end()-1));
                        }
                    }
                    System.out.println("Response received...");
                    System.out.println(response);
                    for (String imagePath : imagePaths) {
                        System.out.println(imagePath);
                        HTTPClient imageRequest = new HTTPClient(httpSocket.getPort(), httpSocket.getInetAddress().getHostName());
                        imageRequest.executeRequest("GET", "www.tinyos.net/"+imagePath, protocol, null);
                    }
                }
            } else if (contentType.startsWith("image")) {
                int index = contentType.indexOf('/');
                String imageType = contentType.substring(index + 1);
                String param = HTTPUtil.parseParams(requestURI);
                String filePath = "res-client/" + param;
                OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
                byte[] data = byteOutput.toByteArray();
                int width = 1; int height = 2;
                DataBuffer dBuff = new DataBufferByte(data, data.length);
                BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

                ImageIO.write(image, "png", new File("image.png"));

                System.out.println("File " + param + " received.");
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset")) return;
            else e.printStackTrace();
        }
    }

    public void closeClient() {
        try {
            this.httpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRequestLine(String method, String uri, String protocol) {
        String path;
        if (protocol.equals("HTTP/1.1")) path = HTTPUtil.parseParams(uri);
        else path = uri;
        return (method + " " + path + " " + protocol + HTTPUtil.CRLF);
    }

    private String getHostHeader(String hostName) {
        return  ("Host: " + hostName + HTTPUtil.CRLF);
    }

}
