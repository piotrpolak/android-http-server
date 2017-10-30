/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/
package ro.polak.http.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.polak.http.Headers;
import ro.polak.http.ServletOutputStreamWrapper;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.utilities.IOUtilities;

/**
 * Represents HTTP response
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpResponseWrapper implements HttpServletResponse {

    private static final String NEW_LINE = "\r\n";
    private static final String TRANSFER_ENCODING_CHUNKED = "chunked";
    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    private static final String CONNECTION_CLOSE = "close";
    private static final Charset CHARSET = Charset.forName("UTF-8");

    private final Serializer<Headers> headersSerializer;
    private final StreamHelper streamHelper;
    private final Serializer<Cookie> cookieHeaderSerializer;

    private Headers headers;
    private OutputStream outputStream;
    private ServletOutputStream wrappedOutputStream;
    private ServletPrintWriter printWriter;
    private boolean isCommitted;
    private List<Cookie> cookies;
    private String status;
    private int bufferSize = 1024;

    /**
     * Default constructor.
     * @param headersSerializer
     * @param cookieHeaderSerializer
     * @param streamHelper
     * @param outputStream
     */
    public HttpResponseWrapper(Serializer<Headers> headersSerializer,
                               Serializer<Cookie> cookieHeaderSerializer,
                               StreamHelper streamHelper,
                               OutputStream outputStream) {
        this.headersSerializer = headersSerializer;
        this.streamHelper = streamHelper;
        this.cookieHeaderSerializer = cookieHeaderSerializer;
        this.outputStream = outputStream;

        wrappedOutputStream = new ServletOutputStreamWrapper(outputStream, this);

        reset();
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public List<Cookie> getCookies() {
        return cookies;
    }

    @Override
    public boolean isCommitted() {
        return isCommitted;
    }

    @Override
    public void reset() {
        headers = new Headers();
        setKeepAlive(false);
        isCommitted = false;
        cookies = new ArrayList<>();
//        resetBuffer();
    }

    @Override
    public void resetBuffer() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void sendRedirect(String location) {
        this.setStatus(HttpServletResponse.STATUS_MOVED_PERMANENTLY);
        headers.setHeader(Headers.HEADER_LOCATION, location);
    }

    @Override
    public void setContentType(String contentType) {
        headers.setHeader(Headers.HEADER_CONTENT_TYPE, contentType);
    }

    @Override
    public void setLocale(Locale loc) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void flushBuffer() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public String getCharacterEncoding() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public String getContentType() {
        return headers.getHeader(Headers.HEADER_CONTENT_TYPE);
    }

    @Override
    public Locale getLocale() {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void setKeepAlive(boolean keepAlive) {
        headers.setHeader(Headers.HEADER_CONNECTION, keepAlive ? CONNECTION_KEEP_ALIVE : CONNECTION_CLOSE);
    }

    @Override
    public void setContentLength(int length) {
        headers.setHeader(Headers.HEADER_CONTENT_LENGTH, Integer.toString(length));
    }

    @Override
    public void setContentLength(long length) {
        headers.setHeader(Headers.HEADER_CONTENT_LENGTH, Long.toString(length));
    }

    @Override
    public Headers getHeaders() {
        return headers;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

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

        byte[] head = (getStatus() + NEW_LINE + headersSerializer.serialize(headers)).getBytes(CHARSET);
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
    public void serveStream(InputStream inputStream) throws IOException {
        streamHelper.serveMultiRangeStream(inputStream, outputStream);
    }

    /**
     * Serves a single range of a stream.
     *
     * @param inputStream
     * @param range
     * @throws IOException
     */
    public void serveStream(InputStream inputStream, Range range) throws IOException {
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
    public void serveStream(InputStream inputStream, List<Range> rangeList, String boundary, String contentType, long totalLength) throws IOException {
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
     * Flushes the output
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
