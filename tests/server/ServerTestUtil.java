package server;

import client.HTTPClient;
import common.HTTP.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

abstract class ServerTestUtil {

    static int PORT = 2626;

    static void createAndExecuteThreadedClient(HTTPMethod method, String uri, HTTPProtocol protocol, String body, HashMap<HTTPField, Object> extraHeaders) {
        RunnableRequest r = new RunnableRequest(method, uri, protocol, body, extraHeaders);
        Thread t = new Thread(r);
        t.start();
    }

    static void createAndExecuteClient(HTTPMethod method, String uri, HTTPProtocol protocol, String body, HashMap<HTTPField, Object> extraHeaders) throws IOException, URISyntaxException {
        URI hostURI = HTTPUtil.makeURI(uri);
        HTTPClient client = new HTTPClient(PORT, hostURI);
        client.executeRequest(method, hostURI, protocol, body != null ? new HTTPBody(body) : null, extraHeaders);
    }

    static HTTPClient createClient(String uri) throws IOException, URISyntaxException {
        HTTPClient client = null;
        URI hostURI = HTTPUtil.makeURI(uri);
        client = new HTTPClient(PORT, hostURI);
        return client;
    }

    static void executeClientRequest(HTTPClient client, HTTPMethod method, String uri, HTTPProtocol protocol, String body, HashMap<HTTPField, Object> extraHeaders) throws IOException, URISyntaxException {
        client.executeRequest(method, HTTPUtil.makeURI(uri), protocol, body != null ? new HTTPBody(body) : null, extraHeaders);
    }

}
