/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import java.util.Set;

/**
 * Serializes headers to text representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class HeadersSerializer {

    private static final String NEW_LINE = "\r\n";

    /**
     * Generates string representation of headers.
     *
     * @return
     */
    public String serialize(Headers headers) {
        Set<String> names = headers.keySet();
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append(name).append(": ").append(headers.getHeader(name)).append(NEW_LINE);
        }
        sb.append(NEW_LINE);

        return sb.toString();
    }
}
