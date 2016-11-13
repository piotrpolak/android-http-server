/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.protocol.exception;

/**
 * Status line exceeds its limit.
 */
public class StatusLineTooLongProtocolException extends ProtocolException {

    public StatusLineTooLongProtocolException(String message) {
        super(message);
    }
}
