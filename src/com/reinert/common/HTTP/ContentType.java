package com.reinert.common.HTTP;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ContentType {

    private final String type;
    private final String extension;
    private final String charSet;

    public static ContentType parseContentType(String str) {
        if (str.matches(".+/.+; charset=.+")) {
            String[] components = str.trim().split("; ");
            String[] typeComps = components[0].split("/");
            String charSet = components[1].substring(8);
            return new ContentType(typeComps[0], typeComps[1], charSet);
        } else if (str.matches(".+/.+")) {
            String[] typeComps = str.split("/");
            return new ContentType(typeComps[0], typeComps[1], null);
        } else {
            return null;
        }
    }

    public static ContentType parseContentTypeFromFile(String filePath) throws IOException {
        System.out.println(Files.probeContentType(Paths.get(filePath)));
        int index = filePath.lastIndexOf('.');
        if (index != -1) {
            String fileType = filePath.substring(index+1);
            switch (fileType) {
                case "html":
                    return new ContentType("text", "html", "utf-8");
                case "txt":
                    return new ContentType("text", "txt", "utf-8");
            }
        }
        return null;
    }

    public ContentType(String type, String extension, String charSet) {
        this.type = type;
        this.extension = extension;
        this.charSet = charSet;
    }

    public String getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }

    public String getCharSet() {
        return charSet;
    }

    @Override
    public String toString() {
        String s = type+"/"+extension;
        if (charSet != null) s += "; charset="+charSet;
        return s;
    }
}
