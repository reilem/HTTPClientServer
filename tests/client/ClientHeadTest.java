package client;

import org.junit.jupiter.api.Test;

public class ClientHeadTest {

    @Test
    void googleHeadTest() {
        ClientTestUtil.doHeadRequest("www.google.com");
    }

    @Test
    void tinyosHeadTest() {
        ClientTestUtil.doHeadRequest("www.tinyos.net");
    }

    @Test
    void wikipediaHeadTest() {
        ClientTestUtil.doHeadRequest("www.wikipedia.com");
    }
}
