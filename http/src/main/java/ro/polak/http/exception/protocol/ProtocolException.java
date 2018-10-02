/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.exception.protocol;

/**
 * Generic protocol exception.
 */
public class ProtocolException extends RuntimeException {

    public ProtocolException(final String message) {
        super(message);
    }

    public ProtocolException(final String s, final Throwable throwable) {
        super(s, throwable);
    }
}
