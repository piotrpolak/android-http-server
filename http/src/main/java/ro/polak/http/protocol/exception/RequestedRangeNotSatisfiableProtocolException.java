/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.protocol.exception;

/**
 * Exception thrown when range is not satisfiable and can not be served.
 */
public class RequestedRangeNotSatisfiableProtocolException extends ProtocolException {

    public RequestedRangeNotSatisfiableProtocolException() {
        super("Range not satisfiable");
    }
}
