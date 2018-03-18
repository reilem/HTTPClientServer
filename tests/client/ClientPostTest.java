package client;

import common.HTTP.HTTPBody;
import common.HTTP.HTTPMethod;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

class ClientPostTest {

    @Test
    void postTest() throws IOException, URISyntaxException {
        URI host = HTTPUtil.makeURI("http://ptsv2.com/t/n3nvz-1521390871/post");
        HTTPClient c = new HTTPClient(80, host);
        c.executeRequest(HTTPMethod.POST, host, HTTPProtocol.HTTP_1_1, new HTTPBody("POST TEST"), null);
    }

}
