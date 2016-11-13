/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.protocol.parser;

/**
 * Generic parser interface.
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
     */
    T parse(String input);
}
