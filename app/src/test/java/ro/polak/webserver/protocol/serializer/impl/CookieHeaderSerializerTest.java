package ro.polak.webserver.protocol.serializer.impl;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.protocol.serializer.impl.CookieHeaderSerializer;
import ro.polak.webserver.servlet.Cookie;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class CookieHeaderSerializerTest {

    private static CookieHeaderSerializer cookieHeaderSerializer;

    @BeforeClass
    public static void setup() {
        cookieHeaderSerializer = new CookieHeaderSerializer();
    }

    @Test
    public void shouldSerializeCookieWithNoAttributes() {
        Cookie cookie = new Cookie("name", "value");
        String serializedCookie = cookieHeaderSerializer.serialize(cookie);
        assertThat(serializedCookie, is("name=value"));
    }

    @Test
    public void shouldSerializeExpiresBasedOnMaxAge() {
        int maxAgeSeconds = 35;

        Cookie cookie = new Cookie("name", "value");
        cookie.setMaxAge(maxAgeSeconds);
        String serializedCookie = cookieHeaderSerializer.serialize(cookie);
        Date date = new Date(System.currentTimeMillis() + maxAgeSeconds * 1000);
        String expiresValue = Utilities.dateFormat(date);
        assertThat(getExpiresValue(serializedCookie), is(expiresValue));
    }

    @Test
    public void shouldSerializeCookieWithAllAttributes() {
        Cookie cookie = new Cookie("name", "value");
        cookie.setDomain("example.com");
        cookie.setMaxAge(20);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/somepath");
        cookie.setComment("Some Comment");
        String serializedCookie = cookieHeaderSerializer.serialize(cookie);
        String[] serializedCookieParts = getCookieParts(serializedCookie);

        assertThat(serializedCookie, startsWith("name=value"));
        assertThat(serializedCookie, containsString("Expires"));
        assertThat(getExpiresValue(serializedCookie), endsWith("GMT")); // Pseudo date validation

        assertThat(serializedCookieParts, hasItemInArray("Domain=example.com"));
        assertThat(serializedCookieParts, hasItemInArray("Path=/somepath"));
        assertThat(serializedCookieParts, hasItemInArray("HttpOnly"));
        assertThat(serializedCookieParts, hasItemInArray("Secure"));
        assertThat(serializedCookieParts, hasItemInArray("Comment=Some Comment"));
    }

    @Test
    public void shouldSerializeCookieWithUrlEncode() {
        String value = "= &";
        Cookie cookie = new Cookie("name", value);
        String serializedCookie = cookieHeaderSerializer.serialize(cookie);
        assertThat(serializedCookie, is("name=" + Utilities.URLEncode(value)));
    }

    private String getExpiresValue(String serializedCookie) {
        int expiresStart = serializedCookie.indexOf("Expires=") + 8;
        return serializedCookie.substring(expiresStart, expiresStart + 29);
    }

    private String[] getCookieParts(String serializedCookie) {
        String[] serializedCookieParts = serializedCookie.split(";");
        for (int i = 0; i < serializedCookieParts.length; i++) {
            serializedCookieParts[i] = serializedCookieParts[i].trim();
        }
        return serializedCookieParts;
    }
}