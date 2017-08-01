/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package example;

import java.io.IOException;
import java.nio.charset.Charset;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpServlet;

/**
 * Writing to output stream.
 */
public class Streaming extends HttpServlet {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            byte[] message = "<p>Writing to output stream directly, without chunking.</p>".getBytes(CHARSET);
            response.setContentLength(message.length);
            response.getOutputStream().write(message);
        } catch (IOException e) {
            throw new ServletException("Unable to write to output stream", e);
        }
    }
}
