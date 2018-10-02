/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.exception.protocol;

/**
 * Length required exception.
 */
public class LengthRequiredException extends ProtocolException {
    public LengthRequiredException() {
        super("Length header is required for POST requests.");
    }
}
