/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.Headers;
import ro.polak.webserver.HttpResponseHeaders;
import ro.polak.webserver.Statistics;
import ro.polak.webserver.WebServer;
import ro.polak.webserver.controller.MainController;

/**
 * Represents HTTP response
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class HttpResponseWrapper implements HttpResponse {

    private HttpResponseHeaders headers;
    private OutputStream out;
    private PrintWriter printWriter = null;
    private boolean headersFlushed = false;
    private List<Cookie> cookies = new ArrayList<>();

    /**
     * Default constructor.
     */
    public HttpResponseWrapper() {
        headers = new HttpResponseHeaders();
        setKeepAlive(false);
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
     * Writes byte array to the output
     *
     * @param byteArray byte array
     */
    public void write(byte[] byteArray) {
        try {
            Statistics.addBytesSend(byteArray.length);
            out.write(byteArray);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes string to the output
     *
     * @param s String to be written
     */
    public void write(String s) {
        write(s.getBytes());
    }

    /**
     * Flushes headers, returns false when headers already flushed.
     * <p/>
     * Can be called once per responce, after the fisrt call it "locks"
     *
     * @return true if headers flushed
     */
    public void flushHeaders() throws IllegalStateException {

        // Prevent from flushing headers more than once
        if (headersFlushed) {
            throw new IllegalStateException("Headers already committed");
        }

        headersFlushed = true;

        for (Cookie cookie : cookies) {
            headers.setHeader(Headers.HEADER_SET_COOKIE, getCookieHeaderValue(cookie));
        }

        write(headers.toString());
    }

    /**
     * Returns serialized cookie header value.
     *
     * @param cookie
     * @return
     */
    private String getCookieHeaderValue(Cookie cookie) {

        // TODO test encoding cookie values

        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName()).append("=").append(Utilities.URLEncode(cookie.getValue()));
        if (cookie.getMaxAge() != -1) {
            String expires = WebServer.sdf.format(new java.util.Date(System.currentTimeMillis() + (cookie.getMaxAge() * 1000)));
            sb.append("; Expires=").append(expires);
        }
        if (cookie.getPath() != null) {
            sb.append("; Path=").append(cookie.getPath());
        }
        if (cookie.getDomain() != null) {
            sb.append("; Domain=").append(cookie.getDomain());
        }
        if (cookie.isHttpOnly()) {
            sb.append("; HttpOnly");
        }
        if (cookie.isSecure()) {
            sb.append("; Secure");
        }
        return sb.toString();
    }

    /**
     * Flushes headers and serves the specified file
     *
     * @param file file to be served
     */
    public void serveFile(File file) {

        MainController.getInstance().println(this.getClass(), "Serving file " + file.getPath());

        try {
            setContentLength(file.length());
            FileInputStream inputStream = new FileInputStream(file);
            serveStream(inputStream);
        } catch (FileNotFoundException e) {
            // TODO Throw exception instead of printing the stack trace
            e.printStackTrace();
            // Suppose this was verified and prevented before
        }
    }

    /**
     * Server an asset
     *
     * @param inputStream
     */
    public void serveStream(InputStream inputStream) {

        // Make sure headers are served before the file content
        // If this throws an IllegalStateException, it means you have tried (incorrectly) to flush headers before
        flushHeaders();

        int numberOfBufferReadBytes = 0;
        byte[] buffer = new byte[512];

        try {
            // Reading and flushing the file chunk by chunk
            while ((numberOfBufferReadBytes = inputStream.read(buffer)) != -1) {
                // Writing to buffer
                out.write(buffer, 0, numberOfBufferReadBytes);
                // Flushing the buffer
                out.flush();
                // Incrementing statistics
                Statistics.addBytesSend(numberOfBufferReadBytes);
            }
            // Flushing remaining buffer, just in case
            out.flush();

        } catch (IOException e) {

        }

        try {
            inputStream.close();
        } // Closing file input stream
        catch (IOException e) {
        }
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

    /**
     * Flushes the output
     *
     * @throws IOException
     */
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void sendRedirect(String location) {
        headers.setStatus(HttpResponseHeaders.STATUS_MOVED_PERMANENTLY);
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
    public HttpResponseHeaders getHeaders() {
        return headers;
    }

    @Override
    public void setStatus(String status) {
        headers.setStatus(status);
    }

    @Override
    public PrintWriter getPrintWriter() {
        // Creating print writer if it does not exist
        if (printWriter == null) {
            printWriter = new PrintWriter();
        }

        return printWriter;
    }
}
