package com.reinert.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class HTTPResponse {

    public void handleResponse(InputStream responseInput) throws IOException {

        // Create a response string builder
        HTTPInputStream httpInputStream = new HTTPInputStream(responseInput);
        HTTPHeader header = httpInputStream.getHeader();

        System.out.println(header.getFieldValue(HTTPField.CONTENT_LENGTH));
        System.out.println(header.getFieldValue(HTTPField.CONTENT_TYPE));

//        Boolean connectionOpen = false;
//        int contentLength = 0;
//        String contentType = "text";
//        String charSet = "UTF-8";
//        StringBuilder line = new StringBuilder();
//        int next = responseInput.read();
//        boolean previousCR = false;
//        while (next != -1) {
//            char nextChar = (char)next;
//            if (!previousCR && nextChar == HTTPUtil.CR) {
//                previousCR = true;
//            } else if (previousCR && nextChar == HTTPUtil.LF) {
//                if (line.length() == 0) break;
//                int index = line.toString().indexOf(':');
//                if (index != -1) {
//                    String tag = line.substring(0, index+1).toLowerCase();
//                    String value = line.substring(index+2).toLowerCase();
//                    switch (tag) {
//                        case "content-type:":
//                            int t = value.indexOf(';');
//                            if (t == -1) contentType = value;
//                            else {
//                                int l = value.indexOf('=');
//                                contentType = value.substring(0,t);
//                                charSet = value.substring(l+1).toUpperCase();
//                            }
//                            break;
//                        case "content-length:":
//                            contentLength = Integer.parseInt(value);
//                            break;
//                        case "connection:":
//                            connectionOpen = value.equals("keep-alive");
//                            break;
//                    }
//                }
//                response.append(line);
//                response.append(HTTPUtil.NEW_LINE);
//                line.setLength(0);
//                previousCR = false;
//            } else {
//                line.append(nextChar);
//            }
//            next = responseInput.read();
//        }
//        response.append(HTTPUtil.NEW_LINE);
//        System.out.println("Header received: " + HTTPUtil.NEW_LINE + response);
//        response.setLength(0);


        int bufferSize = (Integer)header.getFieldValue(HTTPField.CONTENT_LENGTH);
        byte[] bodyData = httpInputStream.getBody(bufferSize);
        ContentType contentType = (ContentType)header.getFieldValue(HTTPField.CONTENT_TYPE);

        // Parse the received byte array
        if (contentType.getType().equals("text")) {
            // Make the string contents
            String responseBody = new String(bodyData, contentType.getCharSet());
            // Print results
            System.out.println("Response received...");
            System.out.println(responseBody);
            // If file extension is html
            if (contentType.getExtension().equals("html")) {
                // Parse the sources from image tags
                ArrayList<String> extraPaths = HTMLUtil.getImageURLs(responseBody);
                for (String imagePath : extraPaths) {
                    System.out.println(imagePath);
//                    this.executeRequest("GET", this.host+"/"+imagePath, protocol, null);
                }
            }
        }
//        System.out.println(new String(bodyData));
//        // Write out the file
//        String param = HTTPUtil.parsePath(requestURI);
//        String file = param.equals("/") ? "/index.html" : param;
//        String filePath = "res-client/" + file;
//        FileOutputStream fos = new FileOutputStream(filePath);
//        fos.write(bodyData);
//        // Close connection if needed
//        if (!connectionOpen) {
//            this.close();
//        }
    }

}
