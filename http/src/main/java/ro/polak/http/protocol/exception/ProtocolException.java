/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.protocol.exception;

/**
 * Generic protocol exception.
 */
public abstract class ProtocolException extends Exception {

    public ProtocolException(String message) {
        super(message);
    }
}
