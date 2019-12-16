package ro.polak.http.impl;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import ro.polak.http.servlet.impl.HttpServletResponseImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// CHECKSTYLE.OFF: JavadocType
public class OutputStreamImplTest {

    @Test
    public void shouldFlushHeadersOnFirstUseForInts() throws IOException {
        OutputStream out = mock(OutputStream.class);
        int data = 1;
        HttpServletResponseImpl response = mock(HttpServletResponseImpl.class);
        ServletOutputStreamImpl outW = new ServletOutputStreamImpl(out, response);
        try {
            when(response.isCommitted()).thenReturn(false);
            outW.write(data);
            when(response.isCommitted()).thenReturn(true);
            outW.write(data);
            verify(out, times(2)).write(data);
            verify(response, times(1)).flushHeaders();
        } finally {
            outW.close();
        }
    }

    @Test
    public void shouldFlushHeadersOnFirstUseForBytes() throws IOException {
        OutputStream out = mock(OutputStream.class);

        byte[] data = "Hello World".getBytes(Charset.defaultCharset());

        HttpServletResponseImpl response = mock(HttpServletResponseImpl.class);
        ServletOutputStreamImpl outW = new ServletOutputStreamImpl(out, response);
        try {
            when(response.isCommitted()).thenReturn(false);
            outW.write(data);
            when(response.isCommitted()).thenReturn(true);
            outW.write(data);

            verify(out, times(2)).write(data);
            verify(response, times(1)).flushHeaders();
        } finally {
            outW.close();
        }
    }

    @Test
    public void shouldFlushHeadersOnFirstUseForByteSection() throws IOException {
        OutputStream out = mock(OutputStream.class);

        byte[] data = "Hello World".getBytes(Charset.defaultCharset());

        HttpServletResponseImpl response = mock(HttpServletResponseImpl.class);
        ServletOutputStreamImpl outW = new ServletOutputStreamImpl(out, response);
        try {
            when(response.isCommitted()).thenReturn(false);
            outW.write(data, 0, 1);
            when(response.isCommitted()).thenReturn(true);
            outW.write(data, 0, 1);

            verify(out, times(2)).write(data, 0, 1);
            verify(response, times(1)).flushHeaders();
        } finally {
            outW.close();
        }
    }

    @Test
    public void shouldForwardFlush() throws IOException {
        OutputStream out = mock(OutputStream.class);
        HttpServletResponseImpl response = mock(HttpServletResponseImpl.class);
        ServletOutputStreamImpl outW = new ServletOutputStreamImpl(out, response);
        try {
            outW.flush();
            verify(out, times(1)).flush();
        } finally {
            outW.close();
        }
    }

    @Test
    public void shouldForwardClose() throws IOException {
        OutputStream out = mock(OutputStream.class);
        HttpServletResponseImpl response = mock(HttpServletResponseImpl.class);
        ServletOutputStreamImpl outW = new ServletOutputStreamImpl(out, response);
        outW.close();
        verify(out, times(1)).close();
    }
}
// CHECKSTYLE.ON: JavadocType
