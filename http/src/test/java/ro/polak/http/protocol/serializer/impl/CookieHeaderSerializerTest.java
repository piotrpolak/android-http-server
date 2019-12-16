package ro.polak.http.protocol.serializer.impl;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import ro.polak.http.servlet.Cookie;
import ro.polak.http.utilities.DateProvider;
import ro.polak.http.utilities.DateUtilities;
import ro.polak.http.utilities.StringUtilities;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public class CookieHeaderSerializerTest {

    private static CookieHeaderSerializer cookieHeaderSerializer;
    private static DateProvider dateProvider;

    @Before
    public void setUp() {
        Date now = new Date();
        dateProvider = mock(DateProvider.class);
        when(dateProvider.now()).thenReturn(now);
        cookieHeaderSerializer = new CookieHeaderSerializer(dateProvider);
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
        Date date = new Date(System.currentTimeMillis() + Long.valueOf(maxAgeSeconds) * 1000L);
        String expiresValue = DateUtilities.dateFormat(date);
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
        assertThat(serializedCookie, is("name=" + StringUtilities.urlEncode(value)));
    }

    private String getExpiresValue(final String serializedCookie) {
        String expiresStartString = "Expires=";
        int expiresStart = serializedCookie.indexOf(expiresStartString) + expiresStartString.length();
        String expiredToken = serializedCookie.substring(expiresStart);
        int semicolonPos = expiredToken.indexOf(';');
        if (semicolonPos > -1) {
            return expiredToken.substring(0, semicolonPos).trim();
        }

        return expiredToken.trim();
    }

    private String[] getCookieParts(final String serializedCookie) {
        String[] serializedCookieParts = serializedCookie.split(";");
        for (int i = 0; i < serializedCookieParts.length; i++) {
            serializedCookieParts[i] = serializedCookieParts[i].trim();
        }
        return serializedCookieParts;
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
