package server;

import common.HTTP.HTTPField;
import common.HTTP.HTTPMethod;
import common.HTTP.HTTPProtocol;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class RunnableRequest implements Runnable {

    private final HTTPMethod method;
    private final String uri;
    private final HTTPProtocol protocol;
    private final String body;
    private final HashMap<HTTPField,Object> extra;

    public RunnableRequest(HTTPMethod method, String uri, HTTPProtocol protocol, String body, HashMap<HTTPField, Object> extra) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.body = body;
        this.extra = extra;
    }

    @Override
    public void run() {
        try {
            ServerTestUtil.createAndExecuteClient(this.method, this.uri, this.protocol, this.body, this.extra);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
