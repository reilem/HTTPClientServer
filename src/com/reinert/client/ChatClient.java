package com.reinert.client;

import java.io.IOException;
import java.net.URISyntaxException;

public class ChatClient {
    public static void main(String[] args) {
        // Fetch input parameters
        String method = args[0];
        String uri = args[1];
        int port = Integer.parseInt(args[2]);
        String protocol = args[3];
        try {
            // Make a client
            HTTPClient client = new HTTPClient(port, uri);
            // Execute its request
            client.executeRequest(method, uri, protocol, null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

//        try {
//            URL url = new URL("http://www.tinyos.net/tos-jwall.jpg");
//            InputStream in = null;
//            in = new BufferedInputStream(url.openStream());
//            OutputStream out = new BufferedOutputStream(new FileOutputStream("Image-Porkeri_001.jpg"));
//
//            for ( int i; (i = in.read()) != -1; ) {
//                out.write(i);
//            }
//            in.close();
//            out.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
