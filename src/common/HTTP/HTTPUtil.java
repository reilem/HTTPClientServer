package common.HTTP;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public abstract class HTTPUtil {

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
        String httpStr = addHttp(str);
        if (httpStr.lastIndexOf('/') <= 7) httpStr += '/';
        URL url = new URL(httpStr);
        return new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery());
    }

    private static String addHttp(String str) {
        if (str.startsWith("https://") || str.startsWith("http://")) return str;
        if (str.startsWith("//")) return "http:"+str;
        return "http://"+str;
    }
}


