package ro.polak.http.servlet;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// CHECKSTYLE.OFF: JavadocType
// CHECKSTYLE.OFF: MagicNumber
public class CookieTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowIllegalName() {
        new Cookie(";illegal", "somevalue");
    }

    @Test
    public void shouldWorkGettersAndSetters() {
        Cookie cookie = new Cookie("someName", "someValue");
        cookie.setComment("comment");
        cookie.setDomain("example.com");
        cookie.setPath("/somepath");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        assertThat(cookie.getName(), is("someName"));
        assertThat(cookie.getValue(), is("someValue"));
        cookie.setValue("SomeValue2");
        assertThat(cookie.getValue(), is("SomeValue2"));
        assertThat(cookie.getComment(), is("comment"));
        assertThat(cookie.getDomain(), is("example.com"));
        assertThat(cookie.getPath(), is("/somepath"));
        assertThat(cookie.isSecure(), is(true));
        assertThat(cookie.isHttpOnly(), is(true));
        assertThat(cookie.getMaxAge(), is(-1));
        cookie.setMaxAge(125);
        assertThat(cookie.getMaxAge(), is(125));
    }

    @Test
    public void shouldAllowBooleanValues() {
        Cookie cookie = new Cookie("someName", true);
        assertThat(cookie.getValue(), is("true"));
    }

    @Test
    public void shouldAllowIntValues() {
        Cookie cookie = new Cookie("someName", 14);
        assertThat(cookie.getValue(), is("14"));
    }

    @Test
    public void shouldAllowLongValues() {
        Cookie cookie = new Cookie("someName", 1545454454544844L);
        assertThat(cookie.getValue(), is("1545454454544844"));
    }

    @Test
    public void shouldAllowDoubleValues() {
        Cookie cookie = new Cookie("someName", 22.33);
        assertThat(cookie.getValue(), is("22.33"));
    }
}
// CHECKSTYLE.ON: MagicNumber
// CHECKSTYLE.ON: JavadocType
