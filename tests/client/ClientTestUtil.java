package client;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPMethod;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPUtil;
import common.HTTP.exceptions.TimeOutException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientTestUtil {

    private static int port = 80;

    static void doHeadRequest(String url) throws Exception {
        doRequest(url, HTTPMethod.HEAD, null);
    }

    static void doGetRequest(String url) throws Exception {
        doRequest(url, HTTPMethod.GET, null);
    }

    static void doPutRequest(String url, String body) throws Exception {
        doRequest(url, HTTPMethod.GET, new HTTPBody(body));
    }

    static void doPostRequest(String url, String body) throws Exception {
        doRequest(url, HTTPMethod.GET, new HTTPBody(body));
    }

    private static void doRequest(String url, HTTPMethod method, HTTPBody body) throws Exception {
        try {
            URI hostURI = HTTPUtil.makeURI(url);
            HTTPClient client = new HTTPClient(port, hostURI);
            client.executeRequest(method, hostURI, HTTPProtocol.HTTP_1_1, body, null);
        } catch (IOException | URISyntaxException | TimeOutException e) {
            throw new Exception();
        }
    }
}
