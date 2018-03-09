/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.exception.protocol;

/**
 * Exception thrown when range is not satisfiable and can not be served.
 */
public class RangeNotSatisfiableProtocolException extends ProtocolException {

    public RangeNotSatisfiableProtocolException() {
        super("Range not satisfiable");
    }
}
