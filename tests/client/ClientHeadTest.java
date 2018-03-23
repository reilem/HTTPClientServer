package client;

import org.junit.jupiter.api.Test;

public class ClientHeadTest {

    @Test
    void googleHeadTest() throws Exception {
        ClientTestUtil.doHeadRequest("www.google.com");
    }

    @Test
    void tinyosHeadTest() throws Exception {
        ClientTestUtil.doHeadRequest("www.tinyos.net");
    }

    @Test
    void wikipediaHeadTest() throws Exception {
        ClientTestUtil.doHeadRequest("www.wikipedia.com");
    }
}
