package common.HTTP;

/**
 * Types of HTTP methods that can be used in HTTP requests. Note: not all methods are implemented on the server. But are
 * made available here to prevent exceptions to the client when it attempts to use them.
 */
public enum HTTPMethod {
    GET, PUT, POST, HEAD, DELETE, CONNECT, OPTIONS, TRACE, PATH, BREW;

    /**
     * Parse the given string into the associated HTTPMethod.
     * @param str   Given string to be parsed.
     * @return      A valid HTTPMethod associated with given string.
     */
    public static HTTPMethod parseMethod(String str) { return HTTPMethod.valueOf(str.trim().toUpperCase()); }

    /**
     * Checks if current HTTPMethod may require a body.
     * @return  True if current method is a PUT or POST. Otherwise false.
     */
    public boolean requiresBody() { return this.equals(PUT) || this.equals(POST); }

    /**
     * Checks if current HTTPMethod may require data from a file.
     * @return  True if current method is a GET or HEAD. Otherwise false.
     */
    public boolean requiresFileData() { return this.equals(GET) || this.equals(HEAD); }
}
