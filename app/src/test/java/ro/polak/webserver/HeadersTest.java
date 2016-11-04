package ro.polak.webserver;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class HeadersTest {

    private Headers headers;

    @Before
    public void setup() {
        headers = new Headers();
    }

    @Test
    public void shouldSetAndGetHeaders() {
        headers.setHeader("Cookie", "ABCD");
        assertThat(headers.getHeader("Cookie"), is("ABCD"));

        headers.setHeader("Cookie", "FFF");
        assertThat(headers.getHeader("Cookie"), is("FFF"));

        assertEquals("FFF", headers.getHeader("Cookie"));
    }

    @Test
    public void shouldReturnNullValueForInexistentHeader() {
        assertThat(headers.getHeader("Non-existent"), is(nullValue()));
    }
}