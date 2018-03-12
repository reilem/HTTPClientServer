package com.reinert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class HTTPClient {

    private enum Info {
        type, length, connection
    }

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
    public String executeRequest(String method, String requestURI, String protocol, String body) {
        if (this.httpSocket == null) return "Invalid socket.";
        System.out.println("Executing request...");
        StringBuilder output = new StringBuilder();
        try {
            // Open request & response streams
            BufferedWriter bufferedRequest = new BufferedWriter(new OutputStreamWriter(httpSocket.getOutputStream()));
            InputStream input = httpSocket.getInputStream();
            // Perform request
            String hostName = HTTPUtil.parseHostName(requestURI);
            bufferedRequest.write(getRequestLine(method, requestURI, protocol));
            if (protocol.equals("HTTP/1.1")) bufferedRequest.write(getHostHeader(hostName));
            bufferedRequest.write(getExtraHeaders());
            if (body != null && !body.isEmpty()) {
                bufferedRequest.write(HTTPUtil.CRLF);
                bufferedRequest.write(body);
                bufferedRequest.write(HTTPUtil.CRLF);
            }
            bufferedRequest.write(HTTPUtil.CRLF);
            bufferedRequest.flush();
            System.out.println("Request sent...");

            Boolean connectionOpen = null;
            Long contentLength = null;
            String contentType = "text";
            String charSet = "utf-8";
            StringBuilder line = new StringBuilder();
            int next = input.read();
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
                                /**
                                 * Image types: image/jpeg image/png image/gif
                                 * Text: text/html, text/css
                                 * Apps: application/javascript
                                 */
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
                    output.append(line);
                    output.append(HTTPUtil.NEW_LINE);
                    line.setLength(0);
                    previousCR = false;
                } else {
                    line.append(nextChar);
                }
                next = input.read();
            }
            output.append(HTTPUtil.NEW_LINE);

            int bufferSize = contentLength != null ? contentLength.intValue() : 2048;
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(bufferSize);
            int val = input.read(buffer);
            byteOutput.write(buffer, 0, val);

            // Parse the received byte array
            if (contentType.startsWith("text")) {
                String str = byteOutput.toString(charSet);
                output.append(str);
            } else if (contentType.startsWith("image")) {
                int index = contentType.indexOf('/');
                String imageType = contentType.substring(index + 1);
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(byteOutput.toByteArray()));
                ImageIO.write(img, imageType, new File("resources/image."+imageType));
            }

            System.out.println("Response received...");
        } catch (IOException e) {
            if (e.getMessage().equals("Connection reset")) return "Request failed: Connection closed by foreign host.";
            else e.printStackTrace();
        }
        return output.toString();
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

    private String getExtraHeaders() {
        return (
            "Accept: */*" + HTTPUtil.CRLF +
            "Accept-Encoding: gzip" + HTTPUtil.CRLF
        );
    }

    private String getHostHeader(String hostName) {
        return  ("Host: " + hostName + HTTPUtil.CRLF);
    }

}
