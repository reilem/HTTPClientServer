package common;

import com.reinert.common.HTTP.ContentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ContentTypeTest {

    @Test
    void contentTypeParseTestSuccess1() {
        String strToParse = "text/html; charset=UTF-8";
        ContentType parsed = ContentType.parseContentType(strToParse);
        assertNotEquals(parsed, null);
        assert parsed != null;
        assertEquals(parsed.getType(), "text");
        assertEquals(parsed.getCharSet(), "UTF-8");
        assertEquals(parsed.getExtension(), "html");
    }

    @Test
    void contentTypeParseTestSuccess2() {
        String strToParse = "image/png";
        ContentType parsed = ContentType.parseContentType(strToParse);
        assertNotEquals(parsed, null);
        assert parsed != null;
        assertEquals(parsed.getType(), "image");
        assertEquals(parsed.getCharSet(), null);
        assertEquals(parsed.getExtension(), "png");
    }

    @Test
    void contentTypeParseTestFail1() {
        String strToParse = "/iqwfgerhgeh;t";
        ContentType parsed = ContentType.parseContentType(strToParse);
        assertEquals(parsed, null);
    }

    @Test
    void contentTypeParseTestFail2() {
        String strToParse = "wfewf/";
        ContentType parsed = ContentType.parseContentType(strToParse);
        assertEquals(parsed, null);
    }

}