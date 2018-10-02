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

    public ServletException(String s) {
        super(s);
    }

    public ServletException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServletException(Throwable throwable) {
        super(throwable);
    }
}
