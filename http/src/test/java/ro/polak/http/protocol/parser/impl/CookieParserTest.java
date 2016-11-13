package ro.polak.http.protocol.parser.impl;

import org.junit.Test;

import java.util.Map;

import ro.polak.http.protocol.parser.Parser;
import ro.polak.http.servlet.Cookie;
import ro.polak.utilities.Utilities;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

public class CookieParserTest {

    private static Parser<Map<String, Cookie>> cookieParser = new CookieParser();

    @Test
    public void shouldParseCookieHavingSpaceInValue() {
        String value = "value containing spaces";
        Map<String, Cookie> cookies = cookieParser.parse("name=" + value);
        assertThat(cookies, hasKey("name"));
        assertThat(cookies.get("name").getValue(), is(value));
    }

    @Test
    public void shouldParseCookieHavingUrlEncodedValue() {
        String value = "&<>some value";
        Map<String, Cookie> cookies = cookieParser.parse("name=" + Utilities.URLEncode(value));
        assertThat(cookies, hasKey("name"));
        assertThat(cookies.get("name").getValue(), is(value));
    }
}