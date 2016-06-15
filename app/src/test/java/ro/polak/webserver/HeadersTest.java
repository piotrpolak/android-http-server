package ro.polak.webserver;

import org.junit.Test;

import static org.junit.Assert.*;

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
        headers.parse("Cookie: ABCD\nTest: XYZ\nServer: 1");

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
        headers.parse("Cookie\nTest\nServer: Pepis");

        assertFalse(headers.containsHeader("Cookie"));
        assertTrue(headers.containsHeader("Server"));

        assertEquals("Pepis", headers.getHeader("Server"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseDefaultMultipleNewlines() {
        Headers headers = new Headers();
        headers.parse("Cookie: ABCD\n\n\nTest: XYZ\nServer: 1\n\n\n\n");

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
        headers.parse("COOKIE: ABCD\nTEST: XYZ\nSERVER: 1");

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
        headers.parse("COOKIE:ABCD\nTEST:XYZ\nSERVER:1");

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
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\n the\n brown dog.\nCookie: ABCD");

        assertEquals("The Fox Jumps Over the brown dog.", headers.getHeader("Word-Of-The-Day"));

        assertTrue(headers.containsHeader("Cookie"));
        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseWrongMultilineHeaderWithSingleLeadingSpace() {
        Headers headers = new Headers();
        headers.parse(" Word-Of-The-Day: The Fox Jumps Over\n the\n brown dog.\nCookie: ABCD");

        assertEquals(null, headers.getHeader("Word-Of-The-Day"));

        assertTrue(headers.containsHeader("Cookie"));
        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseMultilineHeaderWithSingleLeadingTab() {
        Headers headers = new Headers();
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\n\tthe\n\t brown dog.\nCookie: ABCD");

        assertEquals("The Fox Jumps Over the brown dog.", headers.getHeader("Word-Of-The-Day"));

        assertTrue(headers.containsHeader("Cookie"));
        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }

    @Test
    public void testParseMultilineHeaderWithLeadingSpaces() {
        Headers headers = new Headers();
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\n        the\n        brown dog.\nCookie: ABCD");

        assertEquals("The Fox Jumps Over the brown dog.", headers.getHeader("Word-Of-The-Day"));

        assertTrue(headers.containsHeader("Cookie"));
        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertNull(headers.getHeader("Non-existent"));
    }


    @Test
    public void testParseExtraReturns() {
        Headers headers = new Headers();
        headers.parse("Cookie: ABCD\r\n\rTest: XYZ\r\nServer: 1\n");

        assertTrue(headers.containsHeader("Cookie"));
        assertTrue(headers.containsHeader("Test"));
        assertTrue(headers.containsHeader("Server"));

        assertEquals("ABCD", headers.getHeader("Cookie"));
        assertEquals("XYZ", headers.getHeader("Test"));
        assertEquals("1", headers.getHeader("Server"));
        assertNull(headers.getHeader("Non-existent"));
    }
}