package com.reinert.common.HTTP;

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

    private final String field;

    HTTPField(String field) {
        this.field = field;
    }

    public static HTTPField getFieldFor(String str) {
        return HTTPField.valueOf(str.toUpperCase().replace('-', '_'));
    }

    public Object parseValueString(String str) {
        switch (this) {
            case CONTENT_LENGTH: return Integer.parseInt(str);
            case CONTENT_TYPE: return ContentType.parseContentType(str);
            case CONNECTION: return Connection.parseConnection(str);
            case IF_MODIFIED_SINCE: return new HTTPTime(str);
            default: return str;
        }
    }

    public boolean isValidValueType(Object obj) {
        switch (this) {
            case CONTENT_LENGTH: return (obj instanceof Integer);
            case CONTENT_TYPE: return (obj instanceof ContentType);
            case CONNECTION: return (obj instanceof Connection);
            case IF_MODIFIED_SINCE: return (obj instanceof HTTPTime);
            case LAST_MODIFIED: return (obj instanceof HTTPTime);
            case DATE: return (obj instanceof HTTPTime);
            default: return (obj instanceof String);
        }
    }

    @Override
    public String toString() {
        return this.field + ": ";
    }
}