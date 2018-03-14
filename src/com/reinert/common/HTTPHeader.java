package com.reinert.common;

import java.util.HashMap;

public class HTTPHeader {

    private final HTTPProtocol protocol;
    private final HTTPStatus status;
    private HashMap<HTTPField, Object> fields = new HashMap<>();

    public HTTPHeader(HTTPProtocol protocol, HTTPStatus status) {
        this.protocol = protocol;
        this.status = status;
    }

    public void addField(HTTPField field, Object value) {
        if (field != null && value != null)
            fields.put(field, value);
    }

    public Object getFieldValue(HTTPField field) {
        return fields.get(field);
    }

}
