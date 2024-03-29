package server;

import client.HTTPClient;
import common.HTTP.HTTPMethod;
import common.HTTP.HTTPProtocol;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComplexServerTests {

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
    void communicationTest() throws Exception {
        PrintStream stdOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        String uri = "localhost/message.txt";
        HTTPClient client1 = ServerTestUtil.createClient(uri);
        HTTPClient client2 = ServerTestUtil.createClient(uri);
        String okResponse = "HTTP/1.1 200 OK\r\n";
        String msg1 = "Hi number 2.";
        String msg2 = "Hi number 1, how are you?";
        String msg3 = "Not bad, I'm just a client in a test.";
        String msg4 = "Same.";
        ServerTestUtil.executeClientRequest(client1, HTTPMethod.PUT, uri, HTTPProtocol.HTTP_1_1, msg1, null);
        assertTrue(outContent.toString().contains(okResponse));
        outContent.reset();
        ServerTestUtil.executeClientRequest(client2, HTTPMethod.GET, uri, HTTPProtocol.HTTP_1_1, null, null);
        assertTrue(outContent.toString().contains(msg1));
        outContent.reset();
        ServerTestUtil.executeClientRequest(client2, HTTPMethod.PUT, uri, HTTPProtocol.HTTP_1_1, msg2, null);
        assertTrue(outContent.toString().contains(okResponse));
        outContent.reset();
        ServerTestUtil.executeClientRequest(client1, HTTPMethod.GET, uri, HTTPProtocol.HTTP_1_1, null, null);
        assertTrue(outContent.toString().contains(msg2));
        outContent.reset();
        ServerTestUtil.executeClientRequest(client1, HTTPMethod.PUT, uri, HTTPProtocol.HTTP_1_1, msg3, null);
        assertTrue(outContent.toString().contains(okResponse));
        outContent.reset();
        ServerTestUtil.executeClientRequest(client2, HTTPMethod.GET, uri, HTTPProtocol.HTTP_1_1, null, null);
        assertTrue(outContent.toString().contains(msg3));
        outContent.reset();
        ServerTestUtil.executeClientRequest(client2, HTTPMethod.PUT, uri, HTTPProtocol.HTTP_1_1, msg4, null);
        assertTrue(outContent.toString().contains(okResponse));
        outContent.reset();
        ServerTestUtil.executeClientRequest(client1, HTTPMethod.GET, uri, HTTPProtocol.HTTP_1_1, null, null);
        assertTrue(outContent.toString().contains(msg4));
        outContent.reset();
        System.setOut(stdOut);
    }

    @Test
    void dDosTest() {
    }

}
