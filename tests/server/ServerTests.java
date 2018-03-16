package server;

import com.reinert.server.HTTPServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerTests {

    private HTTPServer server;

    @BeforeEach
    public void serverSetup() {
        this.server = new HTTPServer(2626);
        this.server.start();
    }

    @AfterEach
    public void serverShutdown() {
        this.server.stop();
    }

    @Test
    public void simpleGetTest() {

    }


}
