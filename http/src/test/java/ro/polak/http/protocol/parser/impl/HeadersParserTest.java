package ro.polak.http.protocol.parser.impl;

import org.junit.Test;

import ro.polak.http.Headers;
import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

// CHECKSTYLE.OFF: JavadocType
public class HeadersParserTest {

    private static Parser<Headers> headersParser = new HeadersParser();

    @Test
    public void shouldParseSimpleHeaders() throws MalformedInputException {
        Headers headers = headersParser.parse("Cookie: ABCD\r\nTest: XYZ\r\nServer: 1");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
        assertThat(headers.getHeader("Non-existent"), is(nullValue()));
    }

    @Test
    public void shouldParseAndLtrimHeaders() throws MalformedInputException {
        Headers headers = headersParser.parse("Cookie:    ABCD\r\nTest:    XYZ\r\nServer:    1");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
        assertThat(headers.getHeader("Non-existent"), is(nullValue()));
    }

    @Test
    public void shouldParseColonValue() throws MalformedInputException {
        Headers headers = headersParser.parse("Cookie: ABCD:XYZ");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD:XYZ"));
    }

    @Test
    public void shouldParseHeadersWithMissingValues() throws MalformedInputException {
        Headers headers = headersParser.parse("Cookie\r\nTest\r\nServer: Pepis");

        assertThat(headers.containsHeader("Cookie"), is(false));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Server"), is("Pepis"));
    }

    @Test
    public void shouldIgnoreMultipleEmptyNewlines() throws MalformedInputException {
        Headers headers = headersParser.parse("Cookie: ABCD\r\n\r\n\r\nTest: XYZ\r\nServer: 1\r\n\r\n\r\n\r\n");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
    }

    @Test
    public void shouldParseCaseInsensitiveHeaderNames() throws MalformedInputException {
        Headers headers = headersParser.parse("COOKIE: ABCD\r\nTEST: XYZ\r\nSERVER: 1");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
    }

    @Test
    public void shouldParseWithoutLeadingSpace() throws MalformedInputException {
        Headers headers = headersParser.parse("COOKIE:ABCD\r\nTEST:XYZ\r\nSERVER:1");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
    }

    @Test
    public void shouldParseMultilineHeaderWithSingleLeadingSpace() throws MalformedInputException {
        // CHECKSTYLE.OFF: LineLength
        Headers headers = headersParser
                .parse("Word-Of-The-Day: The Fox Jumps Over\r\n the\r\n brown dog.\r\nAnother: Another\r\n multiline\r\n header\r\nCookie: ABCD");
        // CHECKSTYLE.ON: LineLength

        assertThat(headers.containsHeader("Word-Of-The-Day"), is(true));
        assertThat(headers.containsHeader("Another"), is(true));

        assertThat(headers.getHeader("Word-Of-The-Day"), is("The Fox Jumps Over the brown dog."));
        assertThat(headers.getHeader("Another"), is("Another multiline header"));

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD"));
    }

    @Test
    public void shouldParseWrongMultilineHeaderWithSingleLeadingSpace() throws MalformedInputException {
        Headers headers = headersParser
                .parse(" Word-Of-The-Day: The Fox Jumps Over\r\n the\r\n brown dog.\r\nCookie: ABCD");

        assertThat(headers.getHeader("Word-Of-The-Day"), is(nullValue()));

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD"));
    }

    @Test
    public void shouldParseMultilineHeaderWithSingleLeadingTab() throws MalformedInputException {
        Headers headers = headersParser
                .parse("Word-Of-The-Day: The Fox Jumps Over\r\n\tthe\r\n\t brown dog.\r\nCookie: ABCD");

        assertThat(headers.containsHeader("Word-Of-The-Day"), is(true));
        assertThat(headers.getHeader("Word-Of-The-Day"), is("The Fox Jumps Over the brown dog."));

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD"));
    }

    @Test
    public void shouldParseMultilineHeaderWithLeadingSpaces() throws MalformedInputException {
        Headers headers = headersParser
                .parse("Word-Of-The-Day: The Fox Jumps Over\r\n        the\r\n        brown dog.\r\nCookie: ABCD");

        assertThat(headers.containsHeader("Word-Of-The-Day"), is(true));
        assertThat(headers.getHeader("Word-Of-The-Day"), is("The Fox Jumps Over the brown dog."));

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.getHeader("Cookie"), is("ABCD"));
    }

    @Test
    public void shouldParseExtraReturns() throws MalformedInputException {
        Headers headers = headersParser.parse("Cookie: ABCD\r\r\n\rTest: XYZ\r\r\nServer: 1\r\n");

        assertThat(headers.containsHeader("Cookie"), is(true));
        assertThat(headers.containsHeader("Test"), is(true));
        assertThat(headers.containsHeader("Server"), is(true));

        assertThat(headers.getHeader("Cookie"), is("ABCD"));
        assertThat(headers.getHeader("Test"), is("XYZ"));
        assertThat(headers.getHeader("Server"), is("1"));
    }

    @Test
    public void shouldParseMultiValues() throws MalformedInputException {
        Headers headers = headersParser.parse("Accept: application/xml\r\nAccept: application/json\r\n");

        assertThat(headers.containsHeader("Accept"), is(true));
        assertThat(headers.getHeader("Accept"), is("application/xml,application/json"));
    }
}
// CHECKSTYLE.ON: JavadocType
