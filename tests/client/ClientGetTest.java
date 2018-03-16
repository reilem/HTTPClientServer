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
        ClientTestUtil.doGetRequest("www.reddit.com");
    }

    @Test
    void testWithStartTwoSlash() {
        ClientTestUtil.doGetRequest("//www.youtube.com");
    }

    @Test
    void testHTTPStart() {
        ClientTestUtil.doGetRequest("http://www.imgur.com");
    }

    @Test
    void testWithoutWWW() {
        ClientTestUtil.doGetRequest("soundcloud.com");
    }
}
