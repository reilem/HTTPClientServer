package server;

import com.reinert.common.HTTP.HTTPMethod;
import com.reinert.common.HTTP.HTTPProtocol;
import com.reinert.server.HTTPServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

class SimpleServerTests {

    private HTTPServer server;

    @BeforeEach
    void serverSetup() {
        this.server = new HTTPServer(ServerTestUtil.PORT);
        this.server.start();
    }

    @AfterEach
    void serverShutdown() {
        this.server.stop();
    }

    @Test
    void getTest() throws IOException, URISyntaxException {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost/", HTTPProtocol.HTTP_1_1, null, null
        );
    }

    @Test
    void putTest() throws IOException, URISyntaxException {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.PUT, "localhost/test.txt", HTTPProtocol.HTTP_1_1, "PUT TEST\n", null
        );
    }

    @Test
    void postTest() throws IOException, URISyntaxException {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.POST, "localhost/test.txt", HTTPProtocol.HTTP_1_1, "POST TEST\n", null
        );
    }

    @Test
    void headTest() throws IOException, URISyntaxException {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.HEAD, "localhost/", HTTPProtocol.HTTP_1_1, null, null
        );
    }

}
