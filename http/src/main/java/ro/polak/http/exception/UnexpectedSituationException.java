/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/

package ro.polak.http.exception;

/**
 * Generic unrecoverable runtime exception.
 */
public class UnexpectedSituationException extends RuntimeException {

    public UnexpectedSituationException(final String message) {
        super(message);
    }

    public UnexpectedSituationException(final String message, final Throwable e) {
        super(message, e);
    }

    public UnexpectedSituationException(final Throwable e) {
        super(e);
    }
}
