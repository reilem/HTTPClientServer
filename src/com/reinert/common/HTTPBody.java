package com.reinert.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class HTTPBody {

    private final byte[] data;

    HTTPBody(byte[] data) {
        this.data = data;
    }

    public void writeToFile(String filePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(data);
    }

    public String getAsString(String charSet) throws UnsupportedEncodingException {
        if (charSet == null) charSet = "UTF-8";
        return new String(data, charSet);
    }

    public void printData(String charSet) throws UnsupportedEncodingException {
        System.out.println(this.getAsString(charSet));
    }
}
