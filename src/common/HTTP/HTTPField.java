package common.HTTP;

/**
 * Types of HTTP header fields which have an implementation and usage. Any other header fields which do not have
 * an implementation or usage in this code will be indicated by the OTHER type.
 */
public enum HTTPField {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONNECTION("Connection"),
    DATE("Date"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    HOST("Host"),
    LAST_MODIFIED("Last-Modified"),
    LOCATION("Location"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    OTHER("");

    // Field in string format, used during toString() conversion.
    private final String field;

    /**
     * Constructor for a HTTPField enum.
     * @param field String to be used as field value.
     */
    HTTPField(String field) {
        this.field = field;
    }

    /**
     * Parse the given string into a HTTP field.
     * @param str   Given string.
     * @return      HTTPField associated with given string.
     */
    public static HTTPField getFieldFor(String str) {
        return HTTPField.valueOf(str.toUpperCase().replace('-', '_'));
    }

    /**
     * Parse the given value string into an object that is valid for the current HTTPField. For example,
     * CONTENT_LENGTH will attempt to take given string: "123" and parse it into an integer value: 123.
     * @param str   String to be parsed.
     * @return      Valid value object for current HTTPField.
     */
    public Object parseValueString(String str) {
        switch (this) {
            case CONTENT_LENGTH: return Integer.parseInt(str);
            case CONTENT_TYPE: return ContentType.parseContentType(str);
            case CONNECTION: return Connection.parseConnection(str);
            case IF_MODIFIED_SINCE: return new HTTPTime(str);
            case LAST_MODIFIED: return new HTTPTime(str);
            case DATE: return new HTTPTime(str);
            default: return str;
        }
    }

    /**
     * Checks if given object is a valid value type for current HTTPField.
     * @param value   Given object value to be checked.
     * @return        True if value is valid, else false.
     */
    public boolean isValidValueType(Object value) {
        switch (this) {
            case CONTENT_LENGTH: return (value instanceof Integer);
            case CONTENT_TYPE: return (value instanceof ContentType);
            case CONNECTION: return (value instanceof Connection);
            case IF_MODIFIED_SINCE: return (value instanceof HTTPTime);
            case LAST_MODIFIED: return (value instanceof HTTPTime);
            case DATE: return (value instanceof HTTPTime);
            default: return (value instanceof String);
        }
    }

    @Override
    public String toString() {
        return this.field + ": ";
    }
}