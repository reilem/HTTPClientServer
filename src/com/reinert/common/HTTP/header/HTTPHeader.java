package com.reinert.common.HTTP.header;

import com.reinert.common.HTTP.HTTPField;
import com.reinert.common.HTTP.HTTPProtocol;
import com.reinert.common.HTTP.HTTPUtil;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class HTTPHeader {

    protected final HTTPProtocol protocol;

    private HashMap<HTTPField, Object> fields = new HashMap<>();
    private ArrayList<String> other = new ArrayList<>();

    HTTPHeader(HTTPProtocol protocol) {
        this.protocol = protocol;
    }

    public void addField(HTTPField field, Object value) {
        assert field != null;
        if (!field.equals(HTTPField.OTHER) && value != null && field.isValidValueType(value)) {
            fields.put(field, value);
        } else {
            other.add((String)value);
        }

    }

    public Object getFieldValue(HTTPField field) {
        if (!fields.containsKey(field)) return null;
        return fields.get(field);
    }

    public HTTPProtocol getProtocol() {
        return protocol;
    }

    abstract String headerTopLine();

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(this.headerTopLine());
        s.append(HTTPUtil.CRLF);
        this.fields.forEach((HTTPField field, Object value) -> {
            s.append(field.toString());
            s.append(value.toString());
            s.append(HTTPUtil.CRLF);
        });
        this.other.forEach((String str) -> {
            s.append(str);
            s.append(HTTPUtil.CRLF);
        });
        return s.toString();
    }
}