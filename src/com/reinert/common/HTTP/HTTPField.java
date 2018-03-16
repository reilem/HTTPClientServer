package com.reinert.common.HTTP;

import java.time.ZonedDateTime;

public enum HTTPField {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONNECTION("Connection"),
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
            case IF_MODIFIED_SINCE: return ZonedDateTime.parse(str, HTTPUtil.dateFormatter);
            default: return str;
        }
    }

    public boolean isValidValueType(Object obj) {
        switch (this) {
            case CONTENT_LENGTH: return (obj instanceof Integer);
            case CONTENT_TYPE: return (obj instanceof ContentType);
            case CONNECTION: return (obj instanceof Connection);
            case IF_MODIFIED_SINCE: return (obj instanceof ZonedDateTime);
            default: return (obj instanceof String);
        }
    }

    @Override
    public String toString() {
        return this.field + ": ";
    }
}