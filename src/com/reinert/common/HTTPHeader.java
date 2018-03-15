package com.reinert.common;

import java.util.HashMap;

public class HTTPHeader {

    private final HTTPProtocol protocol;
    private final HTTPStatus status;
    private HashMap<HTTPField, Object> fields = new HashMap<>();
    private HashMap<String, String> other = new HashMap<>();

    public HTTPHeader(HTTPProtocol protocol, HTTPStatus status) {
        this.protocol = protocol;
        this.status = status;
    }

    public void addField(HTTPField field, Object value) {
        if (field != null && value != null && field.isValidValueType(value)) {
            fields.put(field, value);
        }
    }

    public void addOther(String key, String value) {
        if (key != null && value != null) {
            other.put(key, value);
        }
    }

    public Object getFieldValue(HTTPField field) {
        if (!fields.containsKey(field)) return null;
        return fields.get(field);
    }

    public HTTPProtocol getProtocol() {
        return protocol;
    }

    public HTTPStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        this.fields.forEach((HTTPField field, Object value) -> {
            s.append(field.toString());
            s.append(value.toString());
            s.append(HTTPUtil.NEW_LINE);
        });
        this.other.forEach((String key, String val) -> {
            s.append(key);
            s.append(": ");
            s.append(val);
            s.append(HTTPUtil.NEW_LINE);
        });
        return s.toString();
    }
}
