package ro.polak.http.servlet;

import org.junit.Test;

public class CookieTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowIllegalName() {
        new Cookie(";illegal", "somevalue");
    }
}