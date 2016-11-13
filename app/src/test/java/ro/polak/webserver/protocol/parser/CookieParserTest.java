package ro.polak.webserver.protocol.parser;

import org.junit.Test;

import java.util.Map;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.servlet.Cookie;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;

public class CookieParserTest {

    private static CookieParser cookieParser = new CookieParser();

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