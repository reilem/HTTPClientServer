package com.reinert.common.HTTP;

public enum HTTPMethod {
    GET, PUT, POST, HEAD;

    public static HTTPMethod parseMethod(String str) {
        return HTTPMethod.valueOf(str.trim().toUpperCase());
    }

    public boolean requiresBody() {
        return this.equals(PUT) || this.equals(POST);
    }
}