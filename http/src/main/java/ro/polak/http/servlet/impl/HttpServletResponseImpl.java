/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/
package ro.polak.http.servlet.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.polak.http.Headers;
import ro.polak.http.impl.ServletOutputStreamImpl;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.servlet.ChunkedPrintWriter;
import ro.polak.http.servlet.Cookie;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.Range;
import ro.polak.http.servlet.ServletOutputStream;
import ro.polak.http.servlet.ServletPrintWriter;
import ro.polak.http.servlet.helper.StreamHelper;
import ro.polak.http.utilities.IOUtilities;

/**
 * Represents HTTP response.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpServletResponseImpl implements HttpServletResponse {

    private static final String NEW_LINE = "\r\n";
    private static final String TRANSFER_ENCODING_CHUNKED = "chunked";
    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    private static final String CONNECTION_CLOSE = "close";
    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private final Serializer<Headers> headersSerializer;
    private final StreamHelper streamHelper;
    private final Serializer<Cookie> cookieHeaderSerializer;
    private final OutputStream outputStream;

    private Headers headers;
    private ServletOutputStream wrappedOutputStream;
    private ServletPrintWriter printWriter;
    private boolean isCommitted;
    private List<Cookie> cookies;
    private String status;
    private int bufferSize = DEFAULT_BUFFER_SIZE;

    /**
     * Default constructor.
     *
     * @param headersSerializer
     * @param cookieHeaderSerializer
     * @param streamHelper
     * @param outputStream
     */
    public HttpServletResponseImpl(final Serializer<Headers> headersSerializer,
                                   final Serializer<Cookie> cookieHeaderSerializer,
                                   final StreamHelper streamHelper,
                                   final OutputStream outputStream) {
        this.headersSerializer = headersSerializer;
        this.streamHelper = streamHelper;
        this.cookieHeaderSerializer = cookieHeaderSerializer;
        this.outputStream = outputStream;

        wrappedOutputStream = new ServletOutputStreamImpl(outputStream, this);

        reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCookie(final Cookie cookie) {
        cookies.add(cookie);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Cookie> getCookies() {
        return cookies;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCommitted() {
        return isCommitted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        headers = new Headers();
        setKeepAlive(false);
        isCommitted = false;
        cookies = new ArrayList<>();
//        resetBuffer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetBuffer() {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCharacterEncoding(final String charset) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRedirect(final String location) {
        this.setStatus(HttpServletResponse.STATUS_MOVED_PERMANENTLY);
        headers.setHeader(Headers.HEADER_LOCATION, location);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentType(final String contentType) {
        headers.setHeader(Headers.HEADER_CONTENT_TYPE, contentType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocale(final Locale loc) {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flushBuffer() {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCharacterEncoding() {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContentType() {
        return headers.getHeader(Headers.HEADER_CONTENT_TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        throw new IllegalStateException("Not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setKeepAlive(final boolean keepAlive) {
        headers.setHeader(Headers.HEADER_CONNECTION, getKeepAliveHeaderValue(keepAlive));
    }

    private String getKeepAliveHeaderValue(final boolean keepAlive) {
        if (keepAlive) {
            return CONNECTION_KEEP_ALIVE;
        }

        return CONNECTION_CLOSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLength(final int length) {
        headers.setHeader(Headers.HEADER_CONTENT_LENGTH, Integer.toString(length));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContentLength(final long length) {
        headers.setHeader(Headers.HEADER_CONTENT_LENGTH, Long.toString(length));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Headers getHeaders() {
        return headers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(final String status) {
        this.status = status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeader(final String name, final String value) {
        headers.setHeader(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIntHeader(final String name, final int value) {
        headers.setHeader(name, Integer.toString(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrintWriter getWriter() {
        if (printWriter == null) {
            if (isTransferChunked()) {
                printWriter = new ChunkedPrintWriter(wrappedOutputStream);
            } else {
                printWriter = new ServletPrintWriter(wrappedOutputStream);
            }
        }

        return printWriter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletOutputStream getOutputStream() {
        return wrappedOutputStream;
    }

    /**
     * Flushes headers, returns false when headers already flushed.
     * <p/>
     * Can be called once per response, after the first call "locks" itself.
     *
     * @return true if headers flushed
     * @throws IllegalStateException when headers have been previously flushed.
     * @throws IOException
     */
    public void flushHeaders() throws IllegalStateException, IOException {
        if (isCommitted) {
            throw new IllegalStateException("Headers should not be committed more than once.");
        }

        isCommitted = true;

        for (Cookie cookie : cookies) {
            headers.setHeader(Headers.HEADER_SET_COOKIE, cookieHeaderSerializer.serialize(cookie));
        }

        byte[] head = (getStatus() + NEW_LINE + headersSerializer.serialize(headers))
                .getBytes(StandardCharsets.UTF_8);
        InputStream inputStream = new ByteArrayInputStream(head);
        serveStream(inputStream);

        IOUtilities.closeSilently(inputStream);
    }

    /**
     * Serves stream.
     *
     * @param inputStream
     * @throws IOException
     */
    public void serveStream(final InputStream inputStream) throws IOException {
        streamHelper.serveMultiRangeStream(inputStream, outputStream);
    }

    /**
     * Serves a single range of a stream.
     *
     * @param inputStream
     * @param range
     * @throws IOException
     */
    public void serveStream(final InputStream inputStream, final Range range) throws IOException {
        streamHelper.serveMultiRangeStream(inputStream, outputStream, range);
    }

    /**
     * Serve multiple ranges of a stream.
     *
     * @param inputStream
     * @param rangeList
     * @param boundary
     * @param contentType
     * @param totalLength
     * @throws IOException
     */
    public void serveStream(final InputStream inputStream,
                            final List<Range> rangeList,
                            final String boundary,
                            final String contentType,
                            final long totalLength) throws IOException {
        streamHelper.serveMultiRangeStream(inputStream, outputStream, rangeList, boundary, contentType, totalLength);
    }

    /**
     * Returns HTTP status.
     *
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * Tells whether the transfer encoding is chunked.
     *
     * @return
     */
    private boolean isTransferChunked() {
        if (!getHeaders().containsHeader(Headers.HEADER_TRANSFER_ENCODING)
                || getHeaders().containsHeader(Headers.HEADER_CONTENT_LENGTH)) {
            return false;
        }

        return getHeaders().getHeader(Headers.HEADER_TRANSFER_ENCODING).equalsIgnoreCase(TRANSFER_ENCODING_CHUNKED);
    }

    /**
     * Flushes the output.
     *
     * @throws IOException
     */
    public void flush() throws IOException {
        // It makes no sense to set chunked encoding if there is no print writer
        if (printWriter != null && printWriter instanceof ChunkedPrintWriter) {
            getHeaders().setHeader(Headers.HEADER_TRANSFER_ENCODING, TRANSFER_ENCODING_CHUNKED);
        }

        if (!isCommitted()) {
            flushHeaders();
        }

        if (printWriter != null) {
            printWriter.writeEnd();
            printWriter.flush();
        }

        outputStream.flush();
    }
}
