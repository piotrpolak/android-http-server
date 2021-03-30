package ro.polak.http.servlet.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.io.OutputStream;

import ro.polak.http.Headers;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.helper.StreamHelper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

// CHECKSTYLE.OFF: JavadocType
public final class HttpServletResponseImplTest {

    private HttpServletResponseImpl httpServletResponseImpl;

    @BeforeEach
    public void setUp() {
        httpServletResponseImpl = new HttpServletResponseImpl(mock(Serializer.class),
                mock(Serializer.class), mock(StreamHelper.class), mock(OutputStream.class));
    }

    @Test
    public void shouldNotAllowHeadersToBeFlushedTwice() throws IOException {
        try {
            httpServletResponseImpl.flushHeaders();
        } catch (IllegalStateException e) {
            fail("Should not throw exception on the first call");
        }

        assertThrows(IllegalStateException.class, new Executable() {
            @Override
            public void execute() throws IllegalStateException, IOException {
                httpServletResponseImpl.flushHeaders();
            }
        });
    }

    @Test
    public void shouldRedirectProperly() {
        String url = "/SomeUrl";
        httpServletResponseImpl.sendRedirect(url);
        assertThat(httpServletResponseImpl.getStatus(), is("HTTP/1.1 301 Moved Permanently"));
        assertThat(httpServletResponseImpl.getHeaders().getHeader(Headers.HEADER_LOCATION), is(url));
    }

    @Test
    public void shouldNotCreateASinglePrintWriter() {
        assertThat(httpServletResponseImpl.getWriter(), is(httpServletResponseImpl.getWriter()));
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
