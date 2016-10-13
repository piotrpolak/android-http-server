package ro.polak.webserver;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpRequestHeadersTest {

    @Test
    public void testDefault() {
        HttpRequestHeaders headers = new HttpRequestHeaders();
        headers.setStatus("GET /home?param1=ABC&param2=123 HTTP/1.1");
        assertEquals("GET", headers.getMethod());
        assertEquals("param1=ABC&param2=123", headers.getQueryString());
        assertEquals("/home?param1=ABC&param2=123", headers.getURI());
        assertEquals("/home", headers.getPath());
        assertEquals("HTTP/1.1", headers.getProtocol());

        // This should not really be tested here
        assertEquals("ABC", headers._get("param1"));
        assertEquals("123", headers._get("param2"));
    }
}