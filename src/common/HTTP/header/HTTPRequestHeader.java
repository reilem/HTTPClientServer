package common.HTTP.header;

import common.HTTP.HTTPMethod;
import common.HTTP.HTTPProtocol;

/**
 * Class for HTTP request headers. Used for both sending requests on client side and receiving requests on server side.
 */
public class HTTPRequestHeader extends HTTPHeader{

    // HTTP Method from the current header's request-line
    private final HTTPMethod method;
    // Path from the current header's request-line
    private final String path;

    /**
     * Constructor for a HTTP request header.
     */
    public HTTPRequestHeader(HTTPMethod method, String path, HTTPProtocol protocol) {
        super(protocol);
        this.method = method;
        this.path = path;
    }

    /**
     * Gets the request header's method.
     */
    public HTTPMethod getMethod() {
        return method;
    }

    /**
     * Gets the request header's path.
     */
    public String getPath() {
        return path;
    }

    @Override
    String getHeaderMainLine() {
        return method.toString() + " " + path + " " + protocol.toString();
    }
}
