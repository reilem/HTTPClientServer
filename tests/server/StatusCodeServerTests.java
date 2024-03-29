package server;

import common.HTTP.HTTPField;
import common.HTTP.HTTPMethod;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusCodeServerTests {

    private HTTPServer server;

    private ByteArrayOutputStream outContent;
    private PrintStream stdOut;

    @BeforeEach
    void serverSetup() throws InterruptedException {
        this.outContent = new ByteArrayOutputStream();
        this.stdOut = System.out;
        System.setOut(new PrintStream(outContent));
        this.server = new HTTPServer(ServerTestUtil.PORT);
        this.server.start();
    }

    @AfterEach
    void serverShutdown() throws InterruptedException {
        this.server.stop();
        System.setOut(this.stdOut);
    }

    @Test
    void noContentTest204() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.POST, "localhost/file_that_does_not_exist.txt", HTTPProtocol.HTTP_1_1, "HELLO", null
        );
        assertTrue(outContent.toString().contains("204 No Content\r\n"));
    }

    @Test
    void notModifiedTest304() throws Exception {
        HashMap<HTTPField, Object> extra = new HashMap<>();
        extra.put(HTTPField.IF_MODIFIED_SINCE, new HTTPTime("Wed, 01 Jan 2020 12:00:00 GMT"));
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost/", HTTPProtocol.HTTP_1_1, null, extra
        );
        assertTrue(outContent.toString().contains("304 Not Modified\r\n"));
    }

    @Test
    void badRequestTest400() throws Exception {
        HashMap<HTTPField, Object> extra = new HashMap<>();
        extra.put(HTTPField.HOST, null);
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost/", HTTPProtocol.HTTP_1_1, null, extra
        );
        assertTrue(outContent.toString().contains("400 Bad Request\r\n"));
    }

    @Test
    void accessForbiddenTest403() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.PUT, "localhost/", HTTPProtocol.HTTP_1_1, "Overwrite the root! >:D", null
        );
        assertTrue(outContent.toString().contains("403 Forbidden\r\n"));
    }

    @Test
    void notFoundTest404() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost/a_file_that_does_not_exist.txt", HTTPProtocol.HTTP_1_1, null, null
        );
        assertTrue(outContent.toString().contains("404 Not Found\r\n"));
    }

    @Test
    void lengthRequiredTest411() throws Exception {
        HashMap<HTTPField, Object> extra = new HashMap<>();
        extra.put(HTTPField.CONTENT_LENGTH, null);
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.PUT, "localhost/test.txt", HTTPProtocol.HTTP_1_1, "Put this text.\n", extra
        );
        assertTrue(outContent.toString().contains("411 Length Required\r\n"));
    }

    @Test
    void teapotTest418() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.BREW, "localhost/file_that_does_not_exist.txt", HTTPProtocol.HTTP_1_1, null, null
        );
        assertTrue(outContent.toString().contains("418 I'm a teapot\r\n"));
    }

    @Test
    void badMethodTest501() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.OPTIONS, "localhost", HTTPProtocol.HTTP_1_1, null, null
        );
        assertTrue(outContent.toString().contains("501 Not Implemented\r\n"));
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.DELETE, "localhost", HTTPProtocol.HTTP_1_1, null, null
        );
        assertTrue(outContent.toString().contains("501 Not Implemented\r\n"));
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.TRACE, "localhost", HTTPProtocol.HTTP_1_1, null, null
        );
        assertTrue(outContent.toString().contains("501 Not Implemented\r\n"));
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.CONNECT, "localhost", HTTPProtocol.HTTP_1_1, null, null
        );
        assertTrue(outContent.toString().contains("501 Not Implemented\r\n"));
    }

    @Test
    void badProtocolTest505() throws Exception {
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost", HTTPProtocol.HTTP_0_9, null, null
        );
        assertTrue(outContent.toString().contains("505 HTTP Version Not Supported\r\n"));
        ServerTestUtil.createAndExecuteClient(
                HTTPMethod.GET, "localhost", HTTPProtocol.HTTP_2_0, null, null
        );
        assertTrue(outContent.toString().contains("505 HTTP Version Not Supported\r\n"));
    }

}
