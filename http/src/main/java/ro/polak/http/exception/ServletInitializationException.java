/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.exception;

/**
 * Servlet initialization exception.
 */
public class ServletInitializationException extends Exception {

    public ServletInitializationException(final Throwable throwable) {
        super(throwable);
    }
}
