package server;

import com.reinert.server.HTTPServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatusCodeServerTests {

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
    void badMethodTest() {

    }

    @Test
    void badProtocolTest() {

    }

}
