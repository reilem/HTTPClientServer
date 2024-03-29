package common.HTML;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * A class containing HTML parsing utilities. Extends HTMLEditorKit.ParserCallback.
 */
public class HTMLUtil extends HTMLEditorKit.ParserCallback {

    // Stores an arrayList of found resources during parsing
    private ArrayList<String> resources = new ArrayList<>();

    /**
     * Finds all image source urls contained in given html string.
     * @param data          HTML data given as string.
     * @return              An arrayList of all image source urls.
     * @throws IOException  If something goes wrong during IO.
     */
    public static ArrayList<String> getImageURLs(String data) throws IOException {
        HTMLEditorKit.Parser parseDel = new ParserDelegator();
        Reader r = new StringReader(data);
        HTMLUtil util = new HTMLUtil();
        parseDel.parse(r, util, true);
        r.close();
        return util.resources;
    }

    @Override
    public void handleSimpleTag(Tag t, MutableAttributeSet a, int pos) {
        if (t.equals(Tag.IMG)) {
            String src = (String)a.getAttribute(HTML.Attribute.SRC);
            resources.add(src);
        }
    }
}
