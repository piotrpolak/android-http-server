package ro.polak.http.servlet;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

import ro.polak.http.Headers;
import ro.polak.http.protocol.serializer.Serializer;

import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class HttpResponseWrapperTest {

    private HttpResponseWrapper httpResponseWrapper;

    @Before
    public void setUp() {
        httpResponseWrapper = new HttpResponseWrapper(mock(Serializer.class),
                mock(Serializer.class), mock(StreamHelper.class), mock(OutputStream.class));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowHeadersToBeFlushedTwice() throws IOException {
        try {
            httpResponseWrapper.flushHeaders();
        } catch (IllegalStateException e) {
            fail("Should not throw exception on the first call");
        }

        httpResponseWrapper.flushHeaders();
    }

    @Test
    public void shouldRedirectProperly() throws IOException {
        String url = "/SomeUrl";
        httpResponseWrapper.sendRedirect(url);
        assertThat(httpResponseWrapper.getStatus(), is("HTTP/1.1 301 Moved Permanently"));
        assertThat(httpResponseWrapper.getHeaders().getHeader(Headers.HEADER_LOCATION), is(url));
    }

    @Test
    public void shouldNotCreateASinglePrintWriter() throws IOException {
        assertThat(httpResponseWrapper.getWriter().equals(httpResponseWrapper.getWriter()), is(true));
    }

    @Test
    public void shouldSetHeadersProperly() {
        httpResponseWrapper.setHeader("StringValue", "value");
        httpResponseWrapper.setIntHeader("IntValue", 1);

        assertThat(httpResponseWrapper.getHeaders().getHeader("StringValue"), is("value"));
        assertThat(httpResponseWrapper.getHeaders().getHeader("IntValue"), is("1"));
    }
}