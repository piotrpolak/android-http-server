/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2017
 **************************************************/

package ro.polak.http.protocol.parser;

/**
 * Generic parser interface.
 *
 * @param <T> the parse output type.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public interface Parser<T> {

    /**
     * Parses input string into the destination format.
     *
     * @param input
     * @return
     * @throws MalformedInputException
     */
    T parse(String input) throws MalformedInputException;
}
