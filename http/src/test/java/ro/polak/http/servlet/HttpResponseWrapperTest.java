package ro.polak.http.servlet;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import ro.polak.http.Headers;

import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpResponseWrapperTest {

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowHeadersToBeFlushedTwice() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream outputStream = mock(OutputStream.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        HttpResponseWrapper httpResponseWrapper = HttpResponseWrapper.createFromSocket(socket);
        try {
            httpResponseWrapper.flushHeaders();
        } catch (IllegalStateException e) {
            fail("Should not throw exception on the first call");
        }

        httpResponseWrapper.flushHeaders();
    }

    @Test
    public void shouldRedirectProperly() throws IOException {
        Socket socket = mock(Socket.class);
        HttpResponseWrapper httpResponseWrapper = HttpResponseWrapper.createFromSocket(socket);

        String url = "/SomeUrl";
        httpResponseWrapper.sendRedirect(url);
        assertThat(httpResponseWrapper.getStatus(), is("HTTP/1.1 301 Moved Permanently"));
        assertThat(httpResponseWrapper.getHeaders().getHeader(Headers.HEADER_LOCATION), is(url));
    }

    @Test
    public void shouldNotCreateASinglePrintWriter() throws IOException {
        Socket socket = mock(Socket.class);
        HttpResponseWrapper httpResponseWrapper = HttpResponseWrapper.createFromSocket(socket);

        assertThat(httpResponseWrapper.getWriter().equals(httpResponseWrapper.getWriter()), is(true));
    }
}