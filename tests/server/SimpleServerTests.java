package server;

import common.HTTP.HTTPMethod;
import common.HTTP.HTTPProtocol;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleServerTests {

    private HTTPServer server;

    @BeforeEach
    void serverSetup() throws InterruptedException {
        this.server = new HTTPServer(ServerTestUtil.PORT);
        this.server.start();
    }

    @AfterEach
    void serverShutdown() throws InterruptedException {
        this.server.stop();
    }

    @Test
    void getTestHTTP1_1() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost/", HTTPProtocol.HTTP_1_1, null, null
        );
    }

    @Test
    void putTestHTTP1_1() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.PUT, "localhost/test.txt", HTTPProtocol.HTTP_1_1, "PUT TEST\n", null
        );
    }

    @Test
    void postTestHTTP1_1() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.POST, "localhost/test.txt", HTTPProtocol.HTTP_1_1, "POST TEST\n", null
        );
    }

    @Test
    void headTestHTTP1_1() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.HEAD, "localhost/", HTTPProtocol.HTTP_1_1, null, null
        );
    }

    @Test
    void getTestHTTP1_0() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost/", HTTPProtocol.HTTP_1_0, null, null
        );
    }

    @Test
    void putTestHTTP1_0() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.PUT, "localhost/test.txt", HTTPProtocol.HTTP_1_0, "PUT TEST\n", null
        );
    }

    @Test
    void postTestHTTP1_0() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.POST, "localhost/test.txt", HTTPProtocol.HTTP_1_0, "POST TEST\n", null
        );
    }

    @Test
    void headTestHTTP1_0() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.HEAD, "localhost/", HTTPProtocol.HTTP_1_0, null, null
        );
    }

}
