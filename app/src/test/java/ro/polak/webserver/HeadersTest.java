package ro.polak.webserver;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class HeadersTest {

    @Test
    public void testHeaderSettingAndGetting() {
        Headers headers = new Headers();
        headers.setHeader("Cookie", "ABCD");
        assertEquals("ABCD", headers.getHeader("Cookie"));
        headers.setHeader("Cookie", "FFF");
        assertEquals("FFF", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testStatusSettingAndGetting() {
        Headers headers = new Headers();
        headers.setStatus("OK");
        assertEquals("OK", headers.getStatus());
    }

    @Test
    public void testParseDefault() {
        Headers headers = new Headers();
        headers.parse("Cookie: ABCD\r\nTest: XYZ\r\nServer: 1");

        assertTrue(headers.containsHeader("Cookie"));
        assertTrue(headers.containsHeader("Test"));
        assertTrue(headers.containsHeader("Server"));

        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertEquals("XYZ", headers.getHeader("Test"));
        assertEquals("1", headers.getHeader("Server"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseColonValue() {
        Headers headers = new Headers();
        headers.parse("Cookie: ABCD:XYZ");

        assertTrue(headers.containsHeader("Cookie"));

        assertEquals("ABCD:XYZ", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseMissingValue() {
        Headers headers = new Headers();
        headers.parse("Cookie\r\nTest\r\nServer: Pepis");

        assertFalse(headers.containsHeader("Cookie"));
        assertTrue(headers.containsHeader("Server"));

        assertEquals("Pepis", headers.getHeader("Server"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseDefaultMultipleNewlines() {
        Headers headers = new Headers();
        headers.parse("Cookie: ABCD\r\n\r\n\r\nTest: XYZ\r\nServer: 1\r\n\r\n\r\n\r\n");

        assertTrue(headers.containsHeader("Cookie"));
        assertTrue(headers.containsHeader("Test"));
        assertTrue(headers.containsHeader("Server"));

        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertEquals("XYZ", headers.getHeader("Test"));
        assertEquals("1", headers.getHeader("Server"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseCaseInsensitivity() {
        Headers headers = new Headers();
        headers.parse("COOKIE: ABCD\r\nTEST: XYZ\r\nSERVER: 1");

        assertTrue(headers.containsHeader("Cookie"));
        assertTrue(headers.containsHeader("Test"));
        assertTrue(headers.containsHeader("Server"));

        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertEquals("XYZ", headers.getHeader("Test"));
        assertEquals("1", headers.getHeader("Server"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseNoLeadingSpace() {
        Headers headers = new Headers();
        headers.parse("COOKIE:ABCD\r\nTEST:XYZ\r\nSERVER:1");

        assertTrue(headers.containsHeader("Cookie"));
        assertTrue(headers.containsHeader("Test"));
        assertTrue(headers.containsHeader("Server"));

        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertEquals("XYZ", headers.getHeader("Test"));
        assertEquals("1", headers.getHeader("Server"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseMultilineHeaderWithSingleLeadingSpace() {
        Headers headers = new Headers();
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\r\n the\r\n brown dog.\r\nAnother: Another\r\n multiline\r\n header\r\nCookie: ABCD");

        assertTrue(headers.containsHeader("Word-Of-The-Day"));
        assertTrue(headers.containsHeader("Another"));

        assertEquals("The Fox Jumps Over the brown dog.", headers.getHeader("Word-Of-The-Day"));
        assertEquals("Another multiline header", headers.getHeader("Another"));

        assertTrue(headers.containsHeader("Cookie"));
        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseWrongMultilineHeaderWithSingleLeadingSpace() {
        Headers headers = new Headers();
        headers.parse(" Word-Of-The-Day: The Fox Jumps Over\r\n the\r\n brown dog.\r\nCookie: ABCD");

        assertEquals(null, headers.getHeader("Word-Of-The-Day"));

        assertTrue(headers.containsHeader("Cookie"));
        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseMultilineHeaderWithSingleLeadingTab() {
        Headers headers = new Headers();
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\r\n\tthe\r\n\t brown dog.\r\nCookie: ABCD");

        assertTrue(headers.containsHeader("Word-Of-The-Day"));
        assertEquals("The Fox Jumps Over the brown dog.", headers.getHeader("Word-Of-The-Day"));

        assertTrue(headers.containsHeader("Cookie"));
        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseMultilineHeaderWithLeadingSpaces() {
        Headers headers = new Headers();
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\r\n        the\r\n        brown dog.\r\nCookie: ABCD");

        assertTrue(headers.containsHeader("Word-Of-The-Day"));
        assertEquals("The Fox Jumps Over the brown dog.", headers.getHeader("Word-Of-The-Day"));

        assertTrue(headers.containsHeader("Cookie"));
        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseExtraReturns() {
        Headers headers = new Headers();
        headers.parse("Cookie: ABCD\r\r\n\rTest: XYZ\r\r\nServer: 1\r\n");

        assertTrue(headers.containsHeader("Cookie"));
        assertTrue(headers.containsHeader("Test"));
        assertTrue(headers.containsHeader("Server"));

        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertEquals("XYZ", headers.getHeader("Test"));
        assertEquals("1", headers.getHeader("Server"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseMultiValues() {
        Headers headers = new Headers();
        headers.parse("Accept: application/xml\r\nAccept: application/json\r\n");
        assertTrue(headers.containsHeader("Accept"));
        assertEquals("application/xml,application/json", headers.getHeader("Accept"));
    }
}