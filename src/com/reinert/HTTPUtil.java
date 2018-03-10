package com.reinert;

public abstract class HTTPUtil {

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String CRLF = "\r\n";

    public static String parseHostName(String uri) {
        int i = uri.lastIndexOf('/');
        if (i != -1) {
            String host = uri.substring(0, i);
            if (host.startsWith("https://")) return host.substring(8);
            if (host.startsWith("http://")) return host.substring(7);
            return host;
        }
        return uri;
    }

    public static String parseParams(String uri) {
        int i = uri.lastIndexOf('/');
        if (i != -1) return uri.substring(i);
        return "/";
    }

}


