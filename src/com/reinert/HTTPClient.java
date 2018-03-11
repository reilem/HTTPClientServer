package com.reinert;

import java.io.*;
import java.net.*;

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
     * @param extraHeaders  extra headers for request, must include CRLF character for newline
     * @param body          the body of the request, must include CRLF character for newline
     * @return              a string containing the HTTP response
     */
    public String executeRequest(String method, String requestURI, String protocol, String extraHeaders, String body) {
        if (this.httpSocket == null) return "Invalid socket.";
        System.out.println("Executing request...");
        StringBuilder output = new StringBuilder();
        try {
            // Open request & response streams
            BufferedWriter bufferedRequest = new BufferedWriter(new OutputStreamWriter(httpSocket.getOutputStream()));
            BufferedReader bufferedResponse = new BufferedReader(new InputStreamReader(httpSocket.getInputStream()));

            // Perform request
            String hostName = HTTPUtil.parseHostName(requestURI);
            bufferedRequest.write(getRequestLine(method, requestURI, protocol));
            if (protocol.equals("HTTP/1.1")) bufferedRequest.write(getHostHeader(hostName));
            if (extraHeaders != null && !extraHeaders.isEmpty()) bufferedRequest.write(extraHeaders);
            if (body != null && !body.isEmpty()) {
                bufferedRequest.write(HTTPUtil.CRLF);
                bufferedRequest.write(body);
            }
            bufferedRequest.write(HTTPUtil.CRLF);
            bufferedRequest.flush();
            System.out.println("Request sent...");

            // Read response
            String nextLine;
            while ((nextLine = bufferedResponse.readLine()) != null) {
                output.append(nextLine);
                output.append(HTTPUtil.NEW_LINE);
                // In some cases the response does not end in a null line but an empty string. Testing on an empty
                // string will cause responses containing empty lines to not be read till completion. As a compromise
                // a check is done if the buffer is still ready each loop. This test cannot be done in the main loop
                // since the buffer will not be ready until a response has been received.
                if (!bufferedResponse.ready()) break;
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

    private String getHostHeader(String hostName) {
        return  ("Host: " + hostName + HTTPUtil.CRLF);
    }

}
