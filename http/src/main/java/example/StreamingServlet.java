/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import ro.polak.http.exception.ServletException;
import ro.polak.http.servlet.HttpServlet;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;

/**
 * Writing to output stream.
 */
public class StreamingServlet extends HttpServlet {

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
        try {
            byte[] message = "<p>Writing to output stream directly, without chunking.</p>".getBytes(StandardCharsets.UTF_8);
            response.setContentLength(message.length);
            response.getOutputStream().write(message);
        } catch (IOException e) {
            throw new ServletException("Unable to write to output stream", e);
        }
    }
}
