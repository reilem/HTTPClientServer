package com.reinert.common;

public abstract class HTTPUtil {

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String CRLF = "\r\n";
    public static final char CR = '\r';
    public static final char LF = '\n';

    public static String parseHostName(String uri) {
        String stripped = stripHttp(uri);
        int i = stripped.indexOf('/');
        if (i != -1) {
            return stripped.substring(0, i);
        }
        return stripped;
    }

    public static String parseParams(String uri) {
        String stripped = stripHttp(uri);
        int i = stripped.indexOf('/');
        if (i != -1) return stripped.substring(i);
        return "/";
    }

    private static String stripHttp(String str) {
        if (str.startsWith("https://")) return str.substring(8);
        if (str.startsWith("http://")) return str.substring(7);
        return str;
    }

}


