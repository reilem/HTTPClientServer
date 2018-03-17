package client;

import org.junit.jupiter.api.Test;

public class ClientGetTest {

    @Test
    void tinyOSGetTest() {
        ClientTestUtil.doGetRequest("www.tinyos.net/");
    }

    @Test
    void googleGetTest() {
        ClientTestUtil.doGetRequest("www.google.com/");
    }

    @Test
    void testWithoutPath() {
        ClientTestUtil.doGetRequest("www.tcpipguide.com");
    }

    @Test
    void testWithStartTwoSlash() {
        ClientTestUtil.doGetRequest("//www.jmarshall.com");
    }

    @Test
    void testHTTPStart() {
        ClientTestUtil.doGetRequest("http://www.tldp.org");
    }

    @Test
    void testWithoutWWW() {
        ClientTestUtil.doGetRequest("linux-ip.net");
    }
}
