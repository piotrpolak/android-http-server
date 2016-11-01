package ro.polak.webserver;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpRequestHeadersTest {

    @Test
    public void shouldParseRequestString() {
        HttpRequestHeaders headers = new HttpRequestHeaders();
        headers.setStatus("GET /home?param1=ABC&param2=123 HTTP/1.1");

        assertThat(headers.getMethod(), is("GET"));
        assertThat(headers.getQueryString(), is("param1=ABC&param2=123"));
        assertThat(headers.getURI(), is("/home?param1=ABC&param2=123"));
        assertThat(headers.getPath(), is("/home"));
        assertThat(headers.getProtocol(), is("HTTP/1.1"));

        // This should not really be tested here
        assertThat(headers._get("param1"), is("ABC"));
        assertThat(headers._get("param2"), is("123"));
    }
}