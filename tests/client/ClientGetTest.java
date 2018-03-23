package client;

import org.junit.jupiter.api.Test;

public class ClientGetTest {

    @Test
    void googleGetTest() throws Exception {
        ClientTestUtil.doGetRequest("www.google.com/");
    }

    @Test
    void testWithoutPath() throws Exception {
        ClientTestUtil.doGetRequest("www.tinyos.net");
    }

    @Test
    void testWithStartTwoSlash() throws Exception {
        ClientTestUtil.doGetRequest("//www.jmarshall.com");
    }

    @Test
    void testHTTPStart() throws Exception {
        ClientTestUtil.doGetRequest("http://www.tldp.org");
    }

    @Test
    void testWithoutWWW() throws Exception {
        ClientTestUtil.doGetRequest("linux-ip.net");
    }
}
