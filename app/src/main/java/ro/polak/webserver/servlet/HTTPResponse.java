/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.io.InputStream;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.HTTPResponseHeaders;
import ro.polak.webserver.Statistics;
import ro.polak.webserver.WebServer;
import ro.polak.webserver.controller.MainController;

import android.content.Context;

/**
 * Represents HTTP response
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public class HTTPResponse {

    private HTTPResponseHeaders headers;
    private OutputStream out;
    private PrintWriter printWriter = null;
    private boolean headersFlushed = false;

    /**
     * Creates and returns a response out of the socket
     *
     * @param socket
     * @return
     */
    public static HTTPResponse createFromSocket(Socket socket) throws IOException {

        HTTPResponse response = new HTTPResponse();
        response.headers = new HTTPResponseHeaders();
        response.out = socket.getOutputStream();
        response.setKeepAlive(false);

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
     * Sets cookie
     *
     * @param cookieName           name of the cookie
     * @param cookieValue          String value of the cookie
     * @param cookieExpiresSeconds expire time in seconds
     * @param cookiePath           path for the cookie
     */
    public void setCookie(String cookieName, String cookieValue, int cookieExpiresSeconds, String cookiePath) {
        String cookie = cookieName + "=" + Utilities.URLEncode(cookieValue);

        // Setting optional expiration time
        if (cookieExpiresSeconds != 0) {
            cookie += "; expires=" + WebServer.sdf.format(new java.util.Date(System.currentTimeMillis() + (cookieExpiresSeconds * 1000)));
        }

        // Setting optional path
        if (cookiePath != null) {
            cookie += "; path=" + cookiePath;
        }

        // Setting the built cookie
        headers.setHeader("Set-Cookie", cookie);
    }

    /**
     * Sets cookie, expires when browser closed
     *
     * @param cookieName  name of the cookie
     * @param cookieValue String value of the cookie
     */
    public void setCookie(String cookieName, String cookieValue) {
        setCookie(cookieName, cookieValue, 0, null);
    }

    /**
     * Sets cookie
     * <p/>
     * Use negative time to remove cookie
     *
     * @param cookieName        name of the cookie
     * @param cookieValue       String value of the cookie
     * @param timeToLiveSeconds time to live in seconds
     */
    public void setCookie(String cookieName, String cookieValue, int timeToLiveSeconds) {
        setCookie(cookieName, cookieValue, timeToLiveSeconds, null);
    }

    /**
     * Removes cookie
     *
     * @param cookieName name of the cookie
     */
    public void removeCookie(String cookieName) {
        setCookie(cookieName, "", -1, null);
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
        write(headers.toString());
    }

    /**
     * Returns a boolean indicating if the response has been committed. A
     * committed response has already had its status code and headers written.
     *
     * @return a boolean indicating if the response has been committed
     */
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

    /**
     * Redirects the request to the specified location.
     *
     * @param location - relative or absolute path (URL)
     */
    public void sendRedirect(String location) {
        headers.setStatus(HTTPResponseHeaders.STATUS_MOVED_PERMANENTLY);
        headers.setHeader("Location", location);
        flushHeaders();
    }

    /**
     * Flushes headers and serves the specified file
     *
     * @param file file to be served
     */
    public void serveFile(File file) {

        MainController.getInstance().println("Serving file " + file.getPath());

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
     * Serves asset file
     *
     * @param asset
     */
    public void serveAsset(String asset) {

        MainController.getInstance().println("Serving asset " + asset);

        try {
            InputStream inputStream = ((Context) MainController.getInstance().getContext()).getResources().getAssets().open(asset);
            serveStream(inputStream);
        } catch (IOException e) {
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

    /**
     * Sets content type
     *
     * @param contentType content type
     */
    public void setContentType(String contentType) {
        headers.setHeader("Content-Type", contentType);
    }

    /**
     * Returns the content type
     *
     * @return
     */
    public String getContentType() {
        return headers.getHeader("Content-Type");
    }

    /**
     * Sets keepAlive
     *
     * @param keepAlive true for keep alive connection
     */
    public void setKeepAlive(boolean keepAlive) {
        if (keepAlive) {
            headers.setHeader("Connection", "keep-alive");
        } else {
            headers.setHeader("Connection", "close");
        }
    }

    /**
     * Sets content length
     *
     * @param length length of content
     */
    public void setContentLength(int length) {
        headers.setHeader("Content-Length", "" + length);
    }

    /**
     * Sets content length
     *
     * @param length length of content
     */
    public void setContentLength(long length) {
        headers.setHeader("Content-Length", "" + length);
    }

    /**
     * Returns the response headers
     *
     * @return
     */
    public HTTPResponseHeaders getHeaders() {
        return headers;
    }

    /**
     * Sets status of response
     *
     * @param status status code and message
     */
    public void setStatus(String status) {
        headers.setStatus(status);
    }

    /**
     * Returns request print writer
     *
     * @return request print writer
     */
    public PrintWriter getPrintWriter() {
        // Creating print writer if it does not exist
        if (printWriter == null) {
            printWriter = new PrintWriter();
        }

        return printWriter;
    }
}
