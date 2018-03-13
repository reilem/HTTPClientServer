package com.reinert.common;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

public class HTMLUtil extends HTMLEditorKit.ParserCallback {

    private ArrayList<String> resources = new ArrayList<>();

    public static ArrayList<String> getImageURLs(String data) {
        HTMLEditorKit.Parser parseDel = new ParserDelegator();
        Reader r = new StringReader(data);
        try {
            HTMLUtil util = new HTMLUtil();
            parseDel.parse(r, util, true);
            r.close();
            return util.resources;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }



    @Override
    public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
        if (t.equals(Tag.IMG)) {
            String src = (String)a.getAttribute(HTML.Attribute.SRC);
            resources.add(src);
        }
    }
}
