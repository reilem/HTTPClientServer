package util;

import com.reinert.server.HTTPServer;

public class RunnableServer implements Runnable {
    @Override
    public void run() {
        HTTPServer server = new HTTPServer(2626);
        server.start();
    }
}
