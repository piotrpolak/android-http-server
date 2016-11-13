package ro.polak.http.servlet;

import org.junit.Test;

public class CookieTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowIllegalName() {
        Cookie cookie = new Cookie(";illegal", "somevalue");
    }
}