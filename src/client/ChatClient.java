package client;

import common.HTTP.HTTPMethod;
import common.HTTP.HTTPProtocol;
import common.HTTP.HTTPUtil;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Main class of the chat client.
 */
public class ChatClient {

    /**
     * The main function of the chat client. Creates a client and executes a single request based on the given parameters.
     * @param args The String array of given parameters. Must contain a HTTP method, a uri, a port and HTTP protocol.
     */
    public static void main(String[] args) {
        // Fetch input parameters
        String method = args[0];
        String uri = args[1];
        int port = Integer.parseInt(args[2]);
        String protocol = args[3];
        try {
            // Make a client
            HTTPClient client = new HTTPClient(port, HTTPUtil.makeURI(uri));
            // Execute its request
            client.executeRequest(HTTPMethod.parseMethod(method), HTTPUtil.makeURI(uri), HTTPProtocol.parseProtocol(protocol), null, null);
        } catch (IOException e) {
            // Catch any IO errors.
            System.err.println("Internal error occurred while sending the request or reading the response. Please try again.");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // Catch any URI parsing errors.
            System.err.println("Invalid URI path or host name given in second argument.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Catch any Protocol/Method parsing errors
            System.err.println("Invalid Protocol or Method given in first or fourth argument.");
            e.printStackTrace();
        }
    }
}
