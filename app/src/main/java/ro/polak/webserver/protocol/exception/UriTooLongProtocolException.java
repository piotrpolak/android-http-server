/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.protocol.exception;

/**
 * URI exceeds URI limit.
 */
public class UriTooLongProtocolException extends ProtocolException {

    public UriTooLongProtocolException(String message) {
        super(message);
    }
}
