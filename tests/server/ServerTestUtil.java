package server;

import client.HTTPClient;
import common.HTTP.*;
import common.HTTP.exceptions.TimeOutException;

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

    static void createAndExecuteClient(HTTPMethod method, String uri, HTTPProtocol protocol, String body, HashMap<HTTPField, Object> extraHeaders) throws Exception {
        URI hostURI = HTTPUtil.makeURI(uri);
        HTTPClient client = new HTTPClient(PORT, hostURI);
        try {
            client.executeRequest(method, hostURI, protocol, body != null ? new HTTPBody(body) : null, extraHeaders);
        } catch (URISyntaxException | TimeOutException e) {
            throw new Exception();
        }
    }

    static HTTPClient createClient(String uri) throws IOException, URISyntaxException {
        return new HTTPClient(PORT, HTTPUtil.makeURI(uri));
    }

    static void executeClientRequest(HTTPClient client, HTTPMethod method, String uri, HTTPProtocol protocol, String body, HashMap<HTTPField, Object> extraHeaders) throws Exception {
        try {
            client.executeRequest(method, HTTPUtil.makeURI(uri), protocol, body != null ? new HTTPBody(body) : null, extraHeaders);
        } catch (URISyntaxException | TimeOutException e) {
            throw new Exception();
        }
    }

}
