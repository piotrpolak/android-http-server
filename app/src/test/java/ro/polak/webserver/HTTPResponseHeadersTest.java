package ro.polak.webserver;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HTTPResponseHeadersTest {

    @Test
    public void shouldDefault() {
        HTTPResponseHeaders headers = new HTTPResponseHeaders();
        headers.setStatus(HTTPResponseHeaders.STATUS_OK);
        headers.setHeader("Header", "Value");

        String result = "HTTP/1.1 200 OK\r\nHeader: Value\r\n\r\n";

        assertThat(headers.toString(), is(result));
    }
}