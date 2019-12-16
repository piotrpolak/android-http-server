package ro.polak.http.protocol.parser.impl;

import org.junit.Test;

import java.util.Map;

import ro.polak.http.protocol.parser.MalformedInputException;
import ro.polak.http.protocol.parser.Parser;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.utilities.StringUtilities;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

// CHECKSTYLE.OFF: JavadocType
public class CookieParserTest {

    private static Parser<Map<String, Cookie>> cookieParser = new CookieParser();

    @Test
    public void shouldParseCookieHavingSpaceInValue() throws MalformedInputException {
        String value = "value containing spaces";
        Map<String, Cookie> cookies = cookieParser.parse("name=" + value);
        assertThat(cookies, hasKey("name"));
        assertThat(cookies.get("name").getValue(), is(value));
    }

    @Test
    public void shouldParseCookieHavingUrlEncodedValue() throws MalformedInputException {
        String value = "&<>some value";
        Map<String, Cookie> cookies = cookieParser.parse("name=" + StringUtilities.urlEncode(value));
        assertThat(cookies, hasKey("name"));
        assertThat(cookies.get("name").getValue(), is(value));
    }

    @Test
    public void shouldTrimCookieNameValue() throws MalformedInputException {
        Map<String, Cookie> cookies = cookieParser.parse(" name =");
        assertThat(cookies, hasKey("name"));
    }

    @Test
    public void shouldParseEmptyValue() throws MalformedInputException {
        Map<String, Cookie> cookies = cookieParser.parse("");
        assertThat(cookies.size(), is(0));
    }

    @Test
    public void shouldReturnZeroSizeForInvalidValue() throws MalformedInputException {
        Map<String, Cookie> cookies = cookieParser.parse("name");
        assertThat(cookies.size(), is(0));
    }

    @Test
    public void shouldReturnZeroSizeForInvalidKey() throws MalformedInputException {
        Map<String, Cookie> cookies = cookieParser.parse(" = value");
        assertThat(cookies.size(), is(0));
    }

    @Test
    public void shouldParseMalformedEmptyValue() throws MalformedInputException {
        Map<String, Cookie> cookies = cookieParser.parse(" ; ");
        assertThat(cookies.size(), is(0));
    }
}
// CHECKSTYLE.ON: JavadocType
