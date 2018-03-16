package com.reinert.common.HTTP;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public abstract class HTTPUtil {

    // Use DateTimeFormatter for better thread safety
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;

    public static final String CHUNKED = "chunked";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String CRLF = "\r\n";

    public static String makeFilePathFromURI(URI uri) {
        return makeFilePathFromPath(uri.getPath());
    }

    public static String makeFilePathFromPath(String path) {
        return path.equals("/") ? "/index.html" : path;
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


