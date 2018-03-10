package com.reinert;

import java.io.*;
import java.net.*;

public class HTTPClient {

    private final int port;

    HTTPClient(int port) {
        this.port = port;
    }

    public String executeRequest(String method, String requestURI, String protocol) {
        String hostName = this.parseHostName(requestURI);

        System.out.println("Executing request...");
        StringBuilder output = new StringBuilder();
        try {
            // Define address based on hostname
            InetAddress address = InetAddress.getByName(hostName);
            // Create socket
            Socket httpSocket = new Socket(address, this.port);
            // Open request & response streams
            BufferedWriter bufferedRequest = new BufferedWriter(new OutputStreamWriter(httpSocket.getOutputStream()));
            BufferedReader bufferedResponse = new BufferedReader(new InputStreamReader(httpSocket.getInputStream()));

            // Perform request
            bufferedRequest.write(getRequestLine(method, requestURI, protocol));
            if (protocol.equals("HTTP/1.1")) {
                bufferedRequest.write(getHostHeader(hostName));
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
            bufferedRequest.close();
            bufferedResponse.close();
            httpSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    private String getRequestLine(String method, String uri, String protocol) {
        String path;
        if (protocol.equals("HTTP/1.1")) path = parseParams(uri);
        else path = uri;
        return (method + " " + path + " " + protocol + HTTPUtil.CRLF);
    }

    private String getHostHeader(String hostName) {
        return  ("Host: " + hostName + HTTPUtil.CRLF);
    }
    private String parseHostName(String url) {
        int i = url.lastIndexOf('/');
        if (i != -1) {
            String host = url.substring(0, i);
            if (host.startsWith("https://")) return host.substring(8);
            if (host.startsWith("http://")) return host.substring(7);
            return host;
        }
        return url;
    }

    private String parseParams(String url) {
        int i = url.lastIndexOf('/');
        if (i != -1) return url.substring(i);
        return "/";
    }
}
