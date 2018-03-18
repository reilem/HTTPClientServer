package common.HTTP;

/**
 * Types of HTTP Protocol versions that can be used during HTTP requests. Note: not all methods are implemented
 * on the server. But are made available here to prevent exceptions to the client when it attempts to use them.
 */
public enum HTTPProtocol {
    HTTP_0_9, HTTP_1_0, HTTP_1_1, HTTP_2_0;

    /**
     * Parse given string into a valid HTTP Protocol.
     * @param str   Given string.
     * @return      Valid HTTP Protocol associated to given String. If given string is empty HTTP/0.9 will be returned.
     */
    public static HTTPProtocol parseProtocol(String str) {
        if (str.equals("")) return HTTPProtocol.HTTP_0_9;
        return HTTPProtocol.valueOf(str.toUpperCase().replaceAll("[/.]", "_"));
    }

    public boolean supportsChunked() {
        return this.equals(HTTP_1_1) || this.equals(HTTP_2_0);
    }

    @Override
    public String toString() {
        switch (this){
            case HTTP_2_0: return "HTTP/2.0";
            case HTTP_1_1: return "HTTP/1.1";
            case HTTP_1_0: return "HTTP/1.0";
            default: return "";
        }
    }
}
