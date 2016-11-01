package ro.polak.webserver;

import org.junit.Test;

import ro.polak.webserver.servlet.HttpResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HttpResponseHeadersTest {

    @Test
    public void shouldSerializeResponse() {
        HttpResponseHeaders headers = new HttpResponseHeaders();
        headers.setStatus(HttpResponse.STATUS_OK);
        headers.setHeader("Header", "Value");

        assertThat(headers.toString(), is("HTTP/1.1 200 OK\r\nHeader: Value\r\n\r\n"));
    }
}