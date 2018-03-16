package com.reinert.common.HTTP;

import java.io.*;

public class HTTPBody {

    private final byte[] data;

    public HTTPBody(String text) {
        this.data = text.getBytes();
    }

    public HTTPBody(byte[] data) {
        this.data = data;
    }

    public void writeDataToFile(String filePath) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            File newFile = new File(filePath);
            if (newFile.getParentFile().mkdirs()) fos = new FileOutputStream(filePath);
        }
        fos.write(data);
    }

    public String getAsString(String charSet) throws UnsupportedEncodingException {
        if (charSet == null) charSet = "UTF-8";
        return new String(data, charSet);
    }

    public void printData(String charSet) throws UnsupportedEncodingException {
        System.out.println(this.getAsString(charSet));
    }

    public byte[] getData() {
        return data;
    }
}
