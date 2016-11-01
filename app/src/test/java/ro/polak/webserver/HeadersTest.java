package ro.polak.webserver;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HeadersTest {

    private Headers headers;

    @Before
    public void setup() {
        headers = new Headers();
    }

    @Test
    public void shouldSetAndGetHeaders() {
        headers.setHeader("Cookie", "ABCD");
        assertThat(headers.getHeader("Cookie"), is("ABCD"));

        headers.setHeader("Cookie", "FFF");
        assertThat(headers.getHeader("Cookie"), is("FFF"));

        assertEquals("FFF", headers.getHeader("Cookie"));

    }

    @Test
    public void shouldReturnNullValueForInexistentHeader() {
        assertThat(headers.getHeader("Non-existent"), is(nullValue()));
    }

    @Test
    public void shouldSetAndGetStatusMessage() {
        headers.setStatus("OK");
        assertThat(headers.getStatus(), is("OK"));
    }

    @Test
    public void shouldParseSimpleHeaders() {
        headers.parse("Cookie: ABCD\r\nTest: XYZ\r\nServer: 1");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
        assertThat(headers.getHeader("Non-existent"), is(nullValue()));
    }

    @Test
    public void shouldParseColonValue() {
        headers.parse("Cookie: ABCD:XYZ");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD:XYZ"));
    }

    @Test
    public void shouldParseHeadersWithMissingValues() {
        headers.parse("Cookie\r\nTest\r\nServer: Pepis");

        assertThat(headers.containsHeader("Cookie"), is(false));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Server"), is("Pepis"));
    }

    @Test
    public void shouldIgnoreMultipleEmptyNewlines() {
        headers.parse("Cookie: ABCD\r\n\r\n\r\nTest: XYZ\r\nServer: 1\r\n\r\n\r\n\r\n");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
    }

    @Test
    public void shouldParseCaseInsensitiveHeaderNames() {
        headers.parse("COOKIE: ABCD\r\nTEST: XYZ\r\nSERVER: 1");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
    }

    @Test
    public void shouldParseWithoutLeadingSpace() {
        headers.parse("COOKIE:ABCD\r\nTEST:XYZ\r\nSERVER:1");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
    }

    @Test
    public void shouldParseMultilineHeaderWithSingleLeadingSpace() {
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\r\n the\r\n brown dog.\r\nAnother: Another\r\n multiline\r\n header\r\nCookie: ABCD");

        assertThat(headers.containsHeader("Word-Of-The-Day"), is(true));
        assertThat(headers.containsHeader("Another"), is(true));

        assertThat(headers.getHeader("Word-Of-The-Day"), is("The Fox Jumps Over the brown dog."));
        assertThat(headers.getHeader("Another"), is("Another multiline header"));

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD"));
    }

    @Test
    public void shouldParseWrongMultilineHeaderWithSingleLeadingSpace() {
        headers.parse(" Word-Of-The-Day: The Fox Jumps Over\r\n the\r\n brown dog.\r\nCookie: ABCD");

        assertThat(headers.getHeader("Word-Of-The-Day"), is(nullValue()));

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD"));
    }

    @Test
    public void shouldParseMultilineHeaderWithSingleLeadingTab() {
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\r\n\tthe\r\n\t brown dog.\r\nCookie: ABCD");

        assertThat(headers.containsHeader("Word-Of-The-Day"), is(true));
        assertEquals("The Fox Jumps Over the brown dog.", headers.getHeader("Word-Of-The-Day"));

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD"));
    }

    @Test
    public void shouldParseMultilineHeaderWithLeadingSpaces() {
        headers.parse("Word-Of-The-Day: The Fox Jumps Over\r\n        the\r\n        brown dog.\r\nCookie: ABCD");

        assertThat(headers.containsHeader("Word-Of-The-Day"), is(true));
        assertEquals("The Fox Jumps Over the brown dog.", headers.getHeader("Word-Of-The-Day"));

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD"));
    }

    @Test
    public void shouldParseExtraReturns() {
        headers.parse("Cookie: ABCD\r\r\n\rTest: XYZ\r\r\nServer: 1\r\n");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
    }

    @Test
    public void shouldParseMultiValues() {
        headers.parse("Accept: application/xml\r\nAccept: application/json\r\n");

        assertThat(headers.containsHeader("Accept"), is(true));
        assertThat(headers.getHeader("Accept"), is("application/xml,application/json"));
    }
}