package client;

import com.reinert.client.HTTPClient;
import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.HTTPMethod;
import com.reinert.common.HTTP.HTTPProtocol;
import com.reinert.common.HTTP.HTTPUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientTestUtil {

    private static int port = 80;

    static void doHeadRequest(String url) {
        doRequest(url, HTTPMethod.HEAD, null);
    }

    static void doGetRequest(String url) {
        doRequest(url, HTTPMethod.GET, null);
    }

    static void doPutRequest(String url, String body) {
        doRequest(url, HTTPMethod.GET, new HTTPBody(body));
    }

    static void doPostRequest(String url, String body) {
        doRequest(url, HTTPMethod.GET, new HTTPBody(body));
    }

    private static void doRequest(String url, HTTPMethod method, HTTPBody body) {
        try {
            URI hostURI = HTTPUtil.makeURI(url);
            HTTPClient client = new HTTPClient(port, hostURI);
            client.executeRequest(method, hostURI, HTTPProtocol.HTTP_1_1, body, null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
