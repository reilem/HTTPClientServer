package com.reinert.common.HTTP;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class HTTPUtil {

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String CRLF = "\r\n";

    public static String parsePath(String uriStr) throws MalformedURLException, URISyntaxException {
        String path = makeURI(uriStr).getPath();
        if (path.startsWith("/")) return path;
        else return "/"+path;
    }

    public static URI makeURI(String str) throws URISyntaxException, MalformedURLException {
        URL url = new URL(addHttp(str));
        return new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery());
    }

    private static String addHttp(String str) {
        if (str.startsWith("//")) return "http:"+str;
        if (!str.startsWith("http://")) return "http://"+str;
        return str;
    }
}

