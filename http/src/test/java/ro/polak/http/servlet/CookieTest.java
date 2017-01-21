package ro.polak.http.servlet;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CookieTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowIllegalName() {
        new Cookie(";illegal", "somevalue");
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
        Cookie cookie = new Cookie("someName", 1545454454544844l);
        assertThat(cookie.getValue(), is("1545454454544844"));
    }

    @Test
    public void shouldAllowDoubleValues() {
        Cookie cookie = new Cookie("someName", 22.33);
        assertThat(cookie.getValue(), is("22.33"));
    }
}