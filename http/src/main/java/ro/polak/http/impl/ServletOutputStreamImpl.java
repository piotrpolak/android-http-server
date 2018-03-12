/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.impl;

import java.io.IOException;
import java.io.OutputStream;

import ro.polak.http.servlet.impl.HttpResponseImpl;
import ro.polak.http.servlet.ServletOutputStream;

/**
 * Adds possibility flush headers capability to the ordinary output stream.
 *
 * @since 201611
 */
public class ServletOutputStreamImpl extends ServletOutputStream {

    private final OutputStream outputStream;
    private final HttpResponseImpl response;

    /**
     * Default constructor.
     *
     * @param outputStream
     * @param httpResponse
     */
    public ServletOutputStreamImpl(final OutputStream outputStream, final HttpResponseImpl httpResponse) {
        this.outputStream = outputStream;
        this.response = httpResponse;
    }

    @Override
    public void write(int b) throws IOException {
        flushHeadersIfPossible();
        outputStream.write(b);
    }

    @Override
    public void write(byte b[]) throws IOException {
        flushHeadersIfPossible();
        outputStream.write(b);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        flushHeadersIfPossible();
        outputStream.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    private void flushHeadersIfPossible() throws IOException {
        if (!response.isCommitted()) {
            response.flushHeaders();
        }
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
