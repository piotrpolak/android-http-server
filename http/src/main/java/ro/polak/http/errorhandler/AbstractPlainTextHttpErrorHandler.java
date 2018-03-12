/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.errorhandler;

import java.io.IOException;

import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.impl.HttpResponseImpl;

/**
 * Abstract Http Error Handler
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201701
 */
public abstract class AbstractPlainTextHttpErrorHandler implements HttpErrorHandler {

    protected final String status;
    protected final String message;

    public AbstractPlainTextHttpErrorHandler(String status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public void serve(HttpServletResponse response) throws IOException {
        response.setStatus(status);
        response.setContentType("text/plain");
        response.setContentLength(message.length());
        response.getWriter().write(message);
        ((HttpResponseImpl) response).flush();
    }
}
