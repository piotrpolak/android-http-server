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

// CHECKSTYLE.OFF: JavadocType
public class HttpServletResponseImplTest {

    private HttpServletResponseImpl httpServletResponseImpl;

    @Before
    public void setUp() {
        httpServletResponseImpl = new HttpServletResponseImpl(mock(Serializer.class),
                mock(Serializer.class), mock(StreamHelper.class), mock(OutputStream.class));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowHeadersToBeFlushedTwice() throws IOException {
        try {
            httpServletResponseImpl.flushHeaders();
        } catch (IllegalStateException e) {
            fail("Should not throw exception on the first call");
        }

        httpServletResponseImpl.flushHeaders();
    }

    @Test
    public void shouldRedirectProperly() throws IOException {
        String url = "/SomeUrl";
        httpServletResponseImpl.sendRedirect(url);
        assertThat(httpServletResponseImpl.getStatus(), is("HTTP/1.1 301 Moved Permanently"));
        assertThat(httpServletResponseImpl.getHeaders().getHeader(Headers.HEADER_LOCATION), is(url));
    }

    @Test
    public void shouldNotCreateASinglePrintWriter() throws IOException {
        assertThat(httpServletResponseImpl.getWriter().equals(httpServletResponseImpl.getWriter()), is(true));
    }

    @Test
    public void shouldSetHeadersProperly() {
        httpServletResponseImpl.setHeader("StringValue", "value");
        httpServletResponseImpl.setIntHeader("IntValue", 1);

        assertThat(httpServletResponseImpl.getHeaders().getHeader("StringValue"), is("value"));
        assertThat(httpServletResponseImpl.getHeaders().getHeader("IntValue"), is("1"));
    }
}
// CHECKSTYLE.ON: JavadocType
