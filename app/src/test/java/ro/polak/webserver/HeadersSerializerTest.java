package ro.polak.webserver;

import org.junit.Test;

import ro.polak.webserver.servlet.HttpResponse;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HeadersSerializerTest {

    @Test
    public void shouldSerializeResponse() {
        Headers headers = new Headers();
        headers.setStatus(HttpResponse.STATUS_OK);
        headers.setHeader("Header", "Value");
        headers.setHeader("SomeOtherHeader", "123");

        HeadersSerializer headersSerializer = new HeadersSerializer();

        assertThat(headersSerializer.serialize(headers), anyOf(
                is("HTTP/1.1 200 OK\r\nSomeOtherHeader: 123\r\nHeader: Value\r\n\r\n"),
                is("HTTP/1.1 200 OK\r\nHeader: Value\r\nSomeOtherHeader: 123\r\n\r\n")
        ));
    }

    @Test
    public void shouldPreserveOriginalCase() {
        Headers headers = new Headers();
        headers.setStatus(HttpResponse.STATUS_OK);
        headers.setHeader("header", "Value");
        headers.setHeader("someOtherHeader", "123");

        HeadersSerializer headersSerializer = new HeadersSerializer();

        assertThat(headersSerializer.serialize(headers), anyOf(
                is("HTTP/1.1 200 OK\r\nsomeOtherHeader: 123\r\nheader: Value\r\n\r\n"),
                is("HTTP/1.1 200 OK\r\nheader: Value\r\nsomeOtherHeader: 123\r\n\r\n")
        ));
    }
}