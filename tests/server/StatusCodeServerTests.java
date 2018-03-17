package server;

import com.reinert.common.HTTP.HTTPField;
import com.reinert.common.HTTP.HTTPMethod;
import com.reinert.common.HTTP.HTTPProtocol;
import com.reinert.server.HTTPServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class StatusCodeServerTests {

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
    void badRequestTest400() {
        HashMap<HTTPField, Object> extra = new HashMap<>();
        extra.put(HTTPField.HOST, null);
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost/", HTTPProtocol.HTTP_1_1, "Overwrite the root! >:D", extra
        );
    }

    @Test
    void accessForbiddenTest403() {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.PUT, "localhost/", HTTPProtocol.HTTP_1_1, "Overwrite the root! >:D", null
        );
    }

    @Test
    void notFoundTest404() {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost/afilethatdoesnotexist.txt", HTTPProtocol.HTTP_1_1, null, null
        );
    }

    @Test
    void lengthRequiredTest411() {
        HashMap<HTTPField, Object> extra = new HashMap<>();
        extra.put(HTTPField.CONTENT_LENGTH, null);
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.PUT, "localhost/test.txt", HTTPProtocol.HTTP_1_1, "Put this text.\n", extra
        );
    }

    @Test
    void badMethodTest501() {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.OPTIONS, "localhost", HTTPProtocol.HTTP_1_1, null, null
        );
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.DELETE, "localhost", HTTPProtocol.HTTP_1_1, null, null
        );
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.TRACE, "localhost", HTTPProtocol.HTTP_1_1, null, null
        );
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.CONNECT, "localhost", HTTPProtocol.HTTP_1_1, null, null
        );
    }

    @Test
    void badProtocolTest505() {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost", HTTPProtocol.HTTP_0_9, null, null
        );
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost", HTTPProtocol.HTTP_2_0, null, null
        );
    }

}
