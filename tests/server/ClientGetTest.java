package server;

import com.reinert.client.HTTPClient;
import com.reinert.common.HTTP.HTTPMethod;
import com.reinert.common.HTTP.HTTPProtocol;
import com.reinert.common.HTTP.HTTPUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientGetTest {

    private final int port = 80;

    @Test
    void tinyOSGetTest() {
        doGetRequest("www.tinyos.net/");
    }

    @Test
    void googleGetTest() {
        doGetRequest("www.google.com/");
    }

    @Test
    void testWithoutPath() {
        doGetRequest("www.reddit.com");
    }

    @Test
    void testWithStartTwoSlash() {
        doGetRequest("//www.youtube.com");
    }

    @Test
    void testHTTPStart() {
        doGetRequest("http://www.imgur.com");
    }

    @Test
    void testWithoutWWW() {
        doGetRequest("soundcloud.com");
    }

    private void doGetRequest(String url) {
        try {
            URI hostURI = HTTPUtil.makeURI(url);
            HTTPClient client = new HTTPClient(this.port, hostURI);
            client.executeRequest(HTTPMethod.GET, hostURI, HTTPProtocol.HTTP_1_1, null, null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
