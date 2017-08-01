package ro.polak.http;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import ro.polak.http.servlet.HttpResponseWrapper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OutputStreamWrapperTest {

    @Test
    public void shouldFlushHeadersOnFirstUseForInts() throws IOException {
        OutputStream out = mock(OutputStream.class);

        int data = 1;

        HttpResponseWrapper response = mock(HttpResponseWrapper.class);
        ServletOutputStreamWrapper outW = new ServletOutputStreamWrapper(out, response);
        when(response.isCommitted()).thenReturn(false);
        outW.write(data);
        when(response.isCommitted()).thenReturn(true);
        outW.write(data);

        verify(out, times(2)).write(data);
        verify(response, times(1)).flushHeaders();
    }

    @Test
    public void shouldFlushHeadersOnFirstUseForBytes() throws IOException {
        OutputStream out = mock(OutputStream.class);

        byte[] data = "Hello World".getBytes(Charset.defaultCharset());

        HttpResponseWrapper response = mock(HttpResponseWrapper.class);
        ServletOutputStreamWrapper outW = new ServletOutputStreamWrapper(out, response);
        when(response.isCommitted()).thenReturn(false);
        outW.write(data);
        when(response.isCommitted()).thenReturn(true);
        outW.write(data);

        verify(out, times(2)).write(data);
        verify(response, times(1)).flushHeaders();
    }

    @Test
    public void shouldFlushHeadersOnFirstUseForByteSection() throws IOException {
        OutputStream out = mock(OutputStream.class);

        byte[] data = "Hello World".getBytes(Charset.defaultCharset());

        HttpResponseWrapper response = mock(HttpResponseWrapper.class);
        ServletOutputStreamWrapper outW = new ServletOutputStreamWrapper(out, response);
        when(response.isCommitted()).thenReturn(false);
        outW.write(data, 0, 1);
        when(response.isCommitted()).thenReturn(true);
        outW.write(data, 0, 1);

        verify(out, times(2)).write(data, 0, 1);
        verify(response, times(1)).flushHeaders();
    }

    @Test
    public void shouldForwardFlush() throws IOException {
        OutputStream out = mock(OutputStream.class);
        HttpResponseWrapper response = mock(HttpResponseWrapper.class);
        ServletOutputStreamWrapper outW = new ServletOutputStreamWrapper(out, response);
        outW.flush();
        verify(out, times(1)).flush();
    }

    @Test
    public void shouldForwardClose() throws IOException {
        OutputStream out = mock(OutputStream.class);
        HttpResponseWrapper response = mock(HttpResponseWrapper.class);
        ServletOutputStreamWrapper outW = new ServletOutputStreamWrapper(out, response);
        outW.close();
        verify(out, times(1)).close();
    }
}