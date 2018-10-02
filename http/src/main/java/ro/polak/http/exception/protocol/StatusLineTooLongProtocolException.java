/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.exception.protocol;

/**
 * Status line exceeds its limit.
 */
public class StatusLineTooLongProtocolException extends ProtocolException {

    public StatusLineTooLongProtocolException(final String message) {
        super(message);
    }
}
