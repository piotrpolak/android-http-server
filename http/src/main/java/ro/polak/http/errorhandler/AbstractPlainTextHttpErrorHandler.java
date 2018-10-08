/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.errorhandler;

import java.io.IOException;

import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;

/**
 * Abstract Http Error Handler.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201701
 */
public abstract class AbstractPlainTextHttpErrorHandler implements HttpErrorHandler {

    private final String status;
    private final String message;

    public AbstractPlainTextHttpErrorHandler(final String status, final String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void serve(final HttpServletResponse response) throws IOException {
        response.setStatus(status);
        response.setContentType("text/plain");
        response.setContentLength(message.length());
        response.getWriter().write(message);
        ((HttpServletResponseImpl) response).flush();
    }

    /**
     * Returns status message.
     * @return
     */
    protected String getStatus() {
        return status;
    }

    /**
     * Returns the message content.
     * @return
     */
    protected String getMessage() {
        return message;
    }
}
