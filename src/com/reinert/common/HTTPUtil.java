package com.reinert.common;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class HTTPUtil {

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String CRLF = "\r\n";
    public static final char CR = '\r';
    public static final char LF = '\n';

    public static String parseHostName(String uriStr) throws MalformedURLException, URISyntaxException {
        return makeURI(uriStr).getHost();
    }

    public static String parsePath(String uri) throws MalformedURLException, URISyntaxException {
        return makeURI(uri).getPath();
    }

    private static URI makeURI(String str) throws URISyntaxException, MalformedURLException {
        URL url = new URL(addHttp(str));
        return new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery());
    }

    private static String addHttp(String str) {
        if (str.startsWith("//")) return "http:"+str;
        if (!str.startsWith("http://")) return "http://"+str;
        return str;
    }

    private static String stripHttp(String str) {
        if (str.startsWith("http://")) return str.substring(7);
        return str;
    }

}


