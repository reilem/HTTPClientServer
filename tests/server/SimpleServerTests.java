package server;

import com.reinert.client.HTTPClient;
import com.reinert.common.HTTP.HTTPBody;
import com.reinert.common.HTTP.HTTPMethod;
import com.reinert.common.HTTP.HTTPProtocol;
import com.reinert.common.HTTP.HTTPUtil;
import com.reinert.server.HTTPServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SimpleServerTests {

    private final int port = 2626;
    private HTTPServer server;

    @BeforeEach
    void serverSetup() {
        this.server = new HTTPServer(this.port);
        this.server.start();
    }

    @AfterEach
    void serverShutdown() {
        this.server.stop();
    }

    @Test
    void getTest() {
        try {
            URI hostURI = HTTPUtil.makeURI("localhost/");
            HTTPClient client = new HTTPClient(this.port, hostURI);
            client.executeRequest(HTTPMethod.GET, hostURI, HTTPProtocol.HTTP_1_1, null, null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    void putTest() {
        try {
            URI hostURI = HTTPUtil.makeURI("localhost/test.txt");
            HTTPClient client = new HTTPClient(this.port, hostURI);
            client.executeRequest(HTTPMethod.PUT, hostURI, HTTPProtocol.HTTP_1_1, new HTTPBody("PUT TEST\n"), null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    void postTest() {
        try {
            URI hostURI = HTTPUtil.makeURI("localhost/test.txt");
            HTTPClient client = new HTTPClient(this.port, hostURI);
            client.executeRequest(HTTPMethod.POST, hostURI, HTTPProtocol.HTTP_1_1, new HTTPBody("POST TEST\n"), null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Test
    void headTest() {
        try {
            URI hostURI = HTTPUtil.makeURI("localhost/");
            HTTPClient client = new HTTPClient(this.port, hostURI);
            client.executeRequest(HTTPMethod.HEAD, hostURI, HTTPProtocol.HTTP_1_1, null, null);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
