/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package example;

import java.io.IOException;

import ro.polak.http.servlet.HttpRequest;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.Servlet;

/**
 * Writing to output stream.
 */
public class Streaming extends Servlet {

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        try {
            byte[] message = "<p>Writing to output stream directly, without chunking.</p>".getBytes();
            response.setContentLength(message.length);
            response.getOutputStream().write(message);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write to output stream", e);
        }

    }
}
