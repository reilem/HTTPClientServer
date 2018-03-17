package com.reinert.common.HTTP;

public enum HTTPMethod {
    GET, PUT, POST, HEAD, DELETE, CONNECT, OPTIONS, TRACE, PATH;

    public static HTTPMethod parseMethod(String str) { return HTTPMethod.valueOf(str.trim().toUpperCase()); }

    public boolean requiresBody() {
        return this.equals(PUT) || this.equals(POST);
    }

    public boolean requiresFileData() { return this.equals(GET) || this.equals(HEAD); }
}
