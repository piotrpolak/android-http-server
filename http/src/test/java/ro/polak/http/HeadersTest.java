package ro.polak.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

// CHECKSTYLE.OFF: JavadocType
public final class HeadersTest {

    private Headers headers;

    @BeforeEach
    public void setUp() {
        headers = new Headers();
    }

    @Test
    public void shouldSetAndGetHeaders() {
        headers.setHeader("Cookie", "ABCD");
        assertThat(headers.getHeader("Cookie"), is("ABCD"));

        headers.setHeader("Cookie", "FFF");
        assertThat(headers.getHeader("Cookie"), is("FFF"));

        assertThat("FFF", is(headers.getHeader("Cookie")));
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
