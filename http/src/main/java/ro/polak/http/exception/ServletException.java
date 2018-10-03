/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.exception;

/**
 * Default servlet exception.
 */
public class ServletException extends Exception {

    public ServletException(final String s) {
        super(s);
    }

    public ServletException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public ServletException(final Throwable throwable) {
        super(throwable);
    }
}
