package com.reinert.common.HTTP;

public enum HTTPField {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONNECTION("Connection"),
    LOCATION("Location"),
    HOST("Host"),
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
            default: return str;
        }
    }

    public boolean isValidValueType(Object obj) {
        switch (this) {
            case CONTENT_LENGTH: return (obj instanceof Integer);
            case CONTENT_TYPE: return (obj instanceof ContentType);
            case CONNECTION: return (obj instanceof Connection);
            default: return (obj instanceof String);
        }
    }

    @Override
    public String toString() {
        return this.field + ": ";
    }
}