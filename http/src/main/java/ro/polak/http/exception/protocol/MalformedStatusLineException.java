/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.exception.protocol;

/**
 * Malformed status line exception.
 */
public class MalformedStatusLineException extends ProtocolException {

    public MalformedStatusLineException(final String message) {
        super(message);
    }
}
