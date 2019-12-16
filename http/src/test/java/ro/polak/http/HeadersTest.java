package ro.polak.http;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

// CHECKSTYLE.OFF: JavadocType
public class HeadersTest {

    private Headers headers;

    @Before
    public void setUp() {
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
    public void shouldSetAndGetHeadersCaseInsensitive() {
        headers.setHeader("Cookie", "ABCD");
        assertThat(headers.getHeader("COOKIE"), is("ABCD"));
    }

    @Test
    public void shouldSetAndContainHeadersCaseInsensitive() {
        headers.setHeader("Cookie", "ABCD");
        assertThat(headers.containsHeader("COOKIE"), is(true));
    }

    @Test
    public void shouldReturnNullValueForInexistentHeader() {
        assertThat(headers.getHeader("Non-existent"), is(nullValue()));
    }

    @Test
    public void shouldHaveHeaderNamesCaseInsensitive() {
        headers.setHeader("Cookie", "ABCD");
        assertThat(headers.keySet().size(), is(1));
        headers.setHeader("COOKIE", "1234");
        assertThat(headers.keySet().size(), is(1));
        assertThat(headers.keySet(), contains("Cookie"));
    }

    @Test
    public void shouldSetLatestValueToTheHeader() {
        headers.setHeader("Cookie", "ABCD");
        assertThat(headers.keySet().size(), is(1));
        headers.setHeader("COOKIE", "1234");
        assertThat(headers.keySet().size(), is(1));
        assertThat(headers.getHeader("Cookie"), is("1234"));
    }
}
// CHECKSTYLE.ON: JavadocType
