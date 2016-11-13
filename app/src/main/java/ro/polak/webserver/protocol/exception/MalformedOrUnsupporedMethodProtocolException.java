/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.protocol.exception;

/**
 * Method too long or unknown (not supported).
 */
public class MalformedOrUnsupporedMethodProtocolException extends ProtocolException {

    public MalformedOrUnsupporedMethodProtocolException(String message) {
        super(message);
    }
}
