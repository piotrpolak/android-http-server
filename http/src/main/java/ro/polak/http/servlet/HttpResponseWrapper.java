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
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ro.polak.http.Headers;
import ro.polak.http.OutputStreamWrapper;
import ro.polak.http.Statistics;
import ro.polak.http.protocol.serializer.Serializer;
import ro.polak.http.protocol.serializer.impl.CookieHeaderSerializer;
import ro.polak.http.protocol.serializer.impl.HeadersSerializer;

/**
 * Represents HTTP response
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpResponseWrapper implements HttpResponse {

    private static final String NEW_LINE = "\r\n";
    private static final String TRANSFER_ENCODING_CHUNKED = "chunked";
    private static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    private static final String CONNECTION_CLOSE = "close";

    private static Charset charset = Charset.forName("UTF-8");
    private static Serializer<Headers> headersSerializer = new HeadersSerializer();
    private static final CookieHeaderSerializer cookieHeaderSerializer = new CookieHeaderSerializer();

    private Headers headers;
    private OutputStream outputStream;
    private OutputStream wrappedOutputStream;
    private ChunkedPrintWriter printWriter;
    private boolean isCommitted;
    private List<Cookie> cookies;
    private String status;

    /**
     * Default constructor.
     */
    public HttpResponseWrapper() {
        headers = new Headers();
        setKeepAlive(false);
        isCommitted = false;
        cookies = new ArrayList<>();
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
    public void sendRedirect(String location) {
        this.setStatus(HttpResponse.STATUS_MOVED_PERMANENTLY);
        headers.setHeader(Headers.HEADER_LOCATION, location);
    }

    @Override
    public void setContentType(String contentType) {
        headers.setHeader(Headers.HEADER_CONTENT_TYPE, contentType);
    }

    @Override
    public String getContentType() {
        return headers.getHeader(Headers.HEADER_CONTENT_TYPE);
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
    public PrintWriter getPrintWriter() {
        if (printWriter == null) {
            printWriter = new ChunkedPrintWriter(wrappedOutputStream);
        }

        return printWriter;
    }

    @Override
    public OutputStream getOutputStream() {
        return wrappedOutputStream;
    }

    /**
     * Creates and returns a response outputStream of the socket
     *
     * @param socket
     * @return
     */
    public static HttpResponseWrapper createFromSocket(Socket socket) throws IOException {
        HttpResponseWrapper response = new HttpResponseWrapper();
        response.outputStream = socket.getOutputStream();
        response.wrappedOutputStream = new OutputStreamWrapper(socket.getOutputStream(), response);
        return response;
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

        byte[] head = (getStatus() + NEW_LINE + headersSerializer.serialize(headers)).getBytes(charset);
        InputStream inputStream = new ByteArrayInputStream(head);
        serveStream(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
        }
    }

    /**
     * Server an asset
     *
     * @param inputStream
     * @throws IOException
     */
    public void serveStream(InputStream inputStream) throws IOException {
        // TODO for best results move it to an external helper
        int numberOfBufferReadBytes;
        byte[] buffer = new byte[512];

        while ((numberOfBufferReadBytes = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, numberOfBufferReadBytes);
            outputStream.flush();

            Statistics.addBytesSend(numberOfBufferReadBytes);
        }
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
        if (!getHeaders().containsHeader(Headers.HEADER_TRANSFER_ENCODING)) {
            return false;
        }

        return getHeaders().getHeader(Headers.HEADER_TRANSFER_ENCODING).toLowerCase().equals(TRANSFER_ENCODING_CHUNKED);
    }

    /**
     * Flushes the output
     *
     * @throws IOException
     */
    public void flush() throws IOException {
        // It makes no sense to set chunked encoding if there is no print writer
        if (printWriter != null) {
            if (!getHeaders().containsHeader(Headers.HEADER_TRANSFER_ENCODING) && !getHeaders().containsHeader(Headers.HEADER_CONTENT_LENGTH)) {
                getHeaders().setHeader(Headers.HEADER_TRANSFER_ENCODING, TRANSFER_ENCODING_CHUNKED);
            }
        }

        if (!isCommitted()) {
            flushHeaders();
        }

        if (printWriter != null) {
            if (isTransferChunked()) {
                printWriter.writeEnd();
            }
            printWriter.flush();
        }

        outputStream.flush();
    }
}
