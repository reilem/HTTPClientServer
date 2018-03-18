package common.HTTP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A convenience class containing content type variables & methods to parse and request content type. Used for storing
 * the content type received in the "Content-Type:" HTTP header field.
 */
public class ContentType {

    // The type of the content (text/image/etc.)
    private final String type;
    // The extension of the content (html/png/jpeg/etc.)
    private final String extension;
    // the character set of the content (utf-8/etc.)
    private final String charSet;

    /**
     * Parse the given string into a valid ContentType.
     * @param str   String to be parsed.
     * @return      A ContentType object containing parameters parsed from given string.
     */
    public static ContentType parseContentType(String str) {
        // If the str is in format similar to "text/html; charset=utf-8"
        if (str.matches(".+/.+; charset=.+")) {
            // Split on "; " and then on "/"
            String[] components = str.trim().split("; ");
            String[] typeComps = components[0].split("/");
            String charSet = components[1].substring(8);
            // Return new content type with split strings
            return new ContentType(typeComps[0], typeComps[1], charSet);
        } else if (str.matches(".+/.+")) {
            // If the str is in a format similar to "text/html"
            String[] typeComps = str.split("/");
            // Return new content type with split strings.
            return new ContentType(typeComps[0], typeComps[1], null);
        } else {
            // Else return null
            return null;
        }
    }

    /**
     * Parse content type from file found from given filePath.
     * @param filePath      String containing filePath.
     * @return              ContentType containing the type parameters of file found at given filePath.
     * @throws IOException  If something goes wrong during file checking.
     */
    public static ContentType parseContentTypeFromFile(String filePath) throws IOException {
        // Get mineType from file at path
        String mimeType = Files.probeContentType(Paths.get(filePath));
        // If mimeType is null return null
        if (mimeType == null) return null;
        // Split mime type string
        String[] split = mimeType.split("/");
        if (split.length >= 2) {
            // Store split values into new content type.
            return new ContentType(split[0], split[1], null);
        }
        // Else return null.
        return null;
    }

    /**
     * Constructor for ContentType.
     * @param type      Type of content
     * @param extension Extension of content
     * @param charSet   Character set of the content.
     */
    public ContentType(String type, String extension, String charSet) {
        this.type = type;
        this.extension = extension;
        this.charSet = charSet;
    }

    /**
     * Gets the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the extension.
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Gets the character set.
     */
    public String getCharSet() {
        return charSet;
    }

    @Override
    public String toString() {
        String s = type+"/"+extension;
        if (charSet != null) s += "; charset="+charSet;
        return s;
    }
}
