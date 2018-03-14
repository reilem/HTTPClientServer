package com.reinert.common;

public enum HTTPProtocol {
    HTTP_1_1, HTTP_1_0;

    public static HTTPProtocol getProtocolFor(String str) {
        return HTTPProtocol.valueOf(str.toUpperCase().replaceAll("[/.]", "_"));
    }
}
