/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.exception;

/**
 * Filter initialization exception.
 */
public class FilterInitializationException extends Exception {

    public FilterInitializationException(final Throwable throwable) {
        super(throwable);
    }
}
