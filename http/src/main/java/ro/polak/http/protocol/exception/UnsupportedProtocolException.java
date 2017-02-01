/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.protocol.exception;

/**
 * Unsupported protocol.
 */
public class UnsupportedProtocolException extends ProtocolException {

    public UnsupportedProtocolException(String message) {
        super(message);
    }
}
