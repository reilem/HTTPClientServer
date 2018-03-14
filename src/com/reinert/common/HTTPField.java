package com.reinert.common;

public enum HTTPField {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    private final String field;

    HTTPField(String field) {
        this.field = field;
    }

    public static HTTPField getFieldFor(String str) {
        return HTTPField.valueOf(str.toUpperCase().replace('-', '_'));
    }

    public Object parseValueString(String str) {
        if (this.equals(CONTENT_LENGTH))
            return Integer.parseInt(str);
        else return str;
    }

    public boolean isValidValueType(Object obj) {
        if (this.equals(CONTENT_LENGTH) && !(obj instanceof Integer)) return false;
        return true;
    }

    @Override
    public String toString() {
        return this.field;
    }
}

class FieldNotFoundException extends Exception {}