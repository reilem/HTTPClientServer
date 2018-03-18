package common.HTTP;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class containing global convenience methods and constants.
 */
public abstract class HTTPUtil {

    // Convenience global constant for carriage-return line-feed symbols.
    public static final String CRLF = "\r\n";

    /**
     * Makes a valid file path from given uri.
     */
    public static String makeFilePathFromURI(URI uri) {
        return makeFilePathFromPath(uri.getPath());
    }

    /**
     * Makes a valid file path string from given string path. If a "/" is given "/index.html" is returned.
     */
    public static String makeFilePathFromPath(String path) {
        return path.equals("/") ? "/index.html" : path;
    }

    /**
     * Makes a URI from given string. String may be in following formats:
     * https://[host]
     * http://[host]
     * //[host]
     * [host]
     * Host names needs not contains "www" and if do not end in a valid path, a "/" will be added.
     * @param str                       Given string to make a uri from.
     * @return                          Parsed URI from given string.
     * @throws URISyntaxException       If something goes wrong during URI creation.
     * @throws MalformedURLException    If something goes wrong during URL creation.
     */
    public static URI makeURI(String str) throws URISyntaxException, MalformedURLException {
        String httpStr = addHttp(str);
        if (httpStr.lastIndexOf('/') <= 7) httpStr += '/';
        URL url = new URL(httpStr);
        return new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery());
    }

    /**
     * Checks if string starts with "https://", "http://" or "//", and will ensure a valid http:// is added if necessary.
     */
    private static String addHttp(String str) {
        if (str.startsWith("https://") || str.startsWith("http://")) return str;
        if (str.startsWith("//")) return "http:"+str;
        return "http://"+str;
    }
}


