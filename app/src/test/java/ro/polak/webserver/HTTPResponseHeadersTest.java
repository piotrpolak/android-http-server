package ro.polak.webserver;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpResponseHeadersTest {

    @Test
    public void shouldDefault() {
        HttpResponseHeaders headers = new HttpResponseHeaders();
        headers.setStatus(HttpResponseHeaders.STATUS_OK);
        headers.setHeader("Header", "Value");

        String result = "HTTP/1.1 200 OK\r\nHeader: Value\r\n\r\n";

        assertThat(headers.toString(), is(result));
    }
}