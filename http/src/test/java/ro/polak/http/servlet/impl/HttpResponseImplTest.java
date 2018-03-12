package ro.polak.http.servlet.impl;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

import ro.polak.http.Headers;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.helper.StreamHelper;

import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class HttpResponseImplTest {

    private HttpResponseImpl httpResponseImpl;

    @Before
    public void setUp() {
        httpResponseImpl = new HttpResponseImpl(mock(Serializer.class),
                mock(Serializer.class), mock(StreamHelper.class), mock(OutputStream.class));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowHeadersToBeFlushedTwice() throws IOException {
        try {
            httpResponseImpl.flushHeaders();
        } catch (IllegalStateException e) {
            fail("Should not throw exception on the first call");
        }

        httpResponseImpl.flushHeaders();
    }

    @Test
    public void shouldRedirectProperly() throws IOException {
        String url = "/SomeUrl";
        httpResponseImpl.sendRedirect(url);
        assertThat(httpResponseImpl.getStatus(), is("HTTP/1.1 301 Moved Permanently"));
        assertThat(httpResponseImpl.getHeaders().getHeader(Headers.HEADER_LOCATION), is(url));
    }

    @Test
    public void shouldNotCreateASinglePrintWriter() throws IOException {
        assertThat(httpResponseImpl.getWriter().equals(httpResponseImpl.getWriter()), is(true));
    }

    @Test
    public void shouldSetHeadersProperly() {
        httpResponseImpl.setHeader("StringValue", "value");
        httpResponseImpl.setIntHeader("IntValue", 1);

        assertThat(httpResponseImpl.getHeaders().getHeader("StringValue"), is("value"));
        assertThat(httpResponseImpl.getHeaders().getHeader("IntValue"), is("1"));
    }
}