/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/

package ro.polak.http.exception;

import java.io.UnsupportedEncodingException;

/**
 * Generic unrecoverable runtime exception.
 */
public class UnexpectedSituationException extends RuntimeException {
    public UnexpectedSituationException(String message) {
        super(message);
    }

    public UnexpectedSituationException(String message, UnsupportedEncodingException e) {
        super(message, e);
    }
}
