package server;

import com.reinert.client.HTTPClient;
import com.reinert.common.HTTP.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

abstract class ServerTestUtil {

    static int PORT = 2626;

    static void createAndExecuteClient(HTTPMethod method, String uri, HTTPProtocol protocol, String body, HashMap<HTTPField, Object> extraHeaders){
        try {
            URI hostURI = HTTPUtil.makeURI(uri);
            HTTPClient client = new HTTPClient(PORT, hostURI);
            client.executeRequest(method, hostURI, protocol, body != null ? new HTTPBody(body) : null, extraHeaders);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    static HTTPClient createClient(String uri) {
        HTTPClient client = null;
        try {
            URI hostURI = HTTPUtil.makeURI(uri);
            client = new HTTPClient(PORT, hostURI);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return client;
    }

    static void executeClientRequest(HTTPClient client, HTTPMethod method, String uri, HTTPProtocol protocol, String body, HashMap<HTTPField, Object> extraHeaders) {
        try {
            client.executeRequest(method, HTTPUtil.makeURI(uri), protocol, body != null ? new HTTPBody(body) : null, extraHeaders);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
