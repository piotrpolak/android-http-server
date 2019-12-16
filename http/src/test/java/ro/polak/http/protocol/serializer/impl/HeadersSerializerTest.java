package ro.polak.http.protocol.serializer.impl;

import org.junit.Test;

import ro.polak.http.Headers;
import ro.polak.http.protocol.serializer.Serializer;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

// CHECKSTYLE.OFF: JavadocType
public class HeadersSerializerTest {

    private static Serializer<Headers> headersSerializer = new HeadersSerializer();

    @Test
    public void shouldSerializeResponse() {
        Headers headers = new Headers();
        headers.setHeader("Header", "Value");
        headers.setHeader("SomeOtherHeader", "123");

        assertThat(headersSerializer.serialize(headers), anyOf(
                is("SomeOtherHeader: 123\r\nHeader: Value\r\n\r\n"),
                is("Header: Value\r\nSomeOtherHeader: 123\r\n\r\n")
        ));
    }

    @Test
    public void shouldPreserveOriginalCase() {
        Headers headers = new Headers();
        headers.setHeader("header", "Value");
        headers.setHeader("someOtherHeader", "123");

        assertThat(headersSerializer.serialize(headers), anyOf(
                is("someOtherHeader: 123\r\nheader: Value\r\n\r\n"),
                is("header: Value\r\nsomeOtherHeader: 123\r\n\r\n")
        ));
    }
}
// CHECKSTYLE.ON: JavadocType
