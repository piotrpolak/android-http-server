/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/
package ro.polak.webserver.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ro.polak.webserver.CookieHeaderSerializer;
import ro.polak.webserver.Headers;
import ro.polak.webserver.HeadersSerializer;
import ro.polak.webserver.Statistics;

/**
 * Represents HTTP response
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpResponseWrapper implements HttpResponse {

    private static final String NEW_LINE = "\r\n";

    private static Charset charset = Charset.forName("UTF-8");
    private static HeadersSerializer headersSerializer = new HeadersSerializer();
    private static final CookieHeaderSerializer cookieHeaderSerializer = new CookieHeaderSerializer();

    private Headers headers;
    private OutputStream out;
    private ChunkedPrintWriter printWriter;
    private boolean headersFlushed;
    private List<Cookie> cookies;
    private String status;

    /**
     * Default constructor.
     */
    public HttpResponseWrapper() {
        headers = new Headers();
        setKeepAlive(false);
        headersFlushed = false;
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
        return headersFlushed;
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
        headers.setHeader(Headers.HEADER_CONNECTION, keepAlive ? "keep-alive" : "close");
    }

    @Override
    public void setContentLength(int length) {
        headers.setHeader(Headers.HEADER_CONTENT_LENGTH, "" + length);
    }

    @Override
    public void setContentLength(long length) {
        headers.setHeader(Headers.HEADER_CONTENT_LENGTH, "" + length);
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
        // Creating print writer if it does not exist
        if (printWriter == null) {
            printWriter = new ChunkedPrintWriter(out);
        }

        return printWriter;
    }

    /**
     * Creates and returns a response out of the socket
     *
     * @param socket
     * @return
     */
    public static HttpResponseWrapper createFromSocket(Socket socket) throws IOException {
        HttpResponseWrapper response = new HttpResponseWrapper();
        response.out = socket.getOutputStream();
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
    private void flushHeaders() throws IllegalStateException, IOException {

        // Prevent from flushing headers more than once
        if (headersFlushed) {
            throw new IllegalStateException("Headers already committed");
        }

        headersFlushed = true;

        for (Cookie cookie : cookies) {
            headers.setHeader(Headers.HEADER_SET_COOKIE, cookieHeaderSerializer.serialize(cookie));
        }

        // TODO Use string builder
        serveStream(new ByteArrayInputStream((getStatus() + NEW_LINE + headersSerializer.serialize(headers)).getBytes(charset)), false);
    }

    /**
     * Flushes headers and serves the specified file
     *
     * @param file file to be served
     * @throws IOException
     */
    public void serveFile(File file) throws IOException {
        setContentLength(file.length());
        FileInputStream inputStream = new FileInputStream(file);
        serveStream(inputStream);
    }

    /**
     * Server an asset
     *
     * @param inputStream
     * @throws IOException
     */
    public void serveStream(InputStream inputStream) throws IOException {
        serveStream(inputStream, false);
    }

    /**
     * @param inputStream
     * @param flushHeaders
     * @throws IOException
     */
    private void serveStream(InputStream inputStream, boolean flushHeaders) throws IOException {
        // Make sure headers are served before the file content
        // If this throws an IllegalStateException, it means you have tried (incorrectly) to flush headers before
        if (flushHeaders) {
            flushHeaders();
        }

        int numberOfBufferReadBytes;
        byte[] buffer = new byte[512];

        while ((numberOfBufferReadBytes = inputStream.read(buffer)) != -1) {
            out.write(buffer, 0, numberOfBufferReadBytes);
            out.flush();

            Statistics.addBytesSend(numberOfBufferReadBytes);
        }
        // Flushing remaining buffer, just in case
        out.flush();

        try {
            inputStream.close();
        } // Closing file input stream
        catch (IOException e) {
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

        return getHeaders().getHeader(Headers.HEADER_TRANSFER_ENCODING).toLowerCase().equals("chunked");
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
                getHeaders().setHeader(Headers.HEADER_TRANSFER_ENCODING, "chunked");
            }
        }

        flushHeaders();

        if (printWriter != null) {
            if (isTransferChunked()) {
                printWriter.writeEnd();
            }
            printWriter.flush();
        }

        out.flush();
    }
}
