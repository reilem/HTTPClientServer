package com.reinert.common.HTTP;

public enum HTTPProtocol {
    HTTP_1_1, HTTP_1_0;

    public static HTTPProtocol parseProtocol(String str) {
        return HTTPProtocol.valueOf(str.toUpperCase().replaceAll("[/.]", "_"));
    }

    @Override
    public String toString() {
        switch (this){
            case HTTP_1_1: return "HTTP/1.1";
            case HTTP_1_0: return "HTTP/1.0";
            default: return "";
        }
    }
}
