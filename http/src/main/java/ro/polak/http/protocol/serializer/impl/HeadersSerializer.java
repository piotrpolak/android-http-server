/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.protocol.serializer.impl;

import java.util.Set;

import ro.polak.http.Headers;
import ro.polak.http.protocol.serializer.Serializer;

/**
 * Serializes headers to text representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201611
 */
public class HeadersSerializer implements Serializer<Headers> {

    private static final String NEW_LINE = "\r\n";
    private static final String KEY_VALUE_SEPARATOR = ": ";

    /**
     * Generates string representation of headers.
     *
     * @return
     */
    @Override
    public String serialize(final Headers headers) {
        Set<String> names = headers.keySet();
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append(name)
                    .append(KEY_VALUE_SEPARATOR)
                    .append(headers.getHeader(name))
                    .append(NEW_LINE);
        }
        sb.append(NEW_LINE);

        return sb.toString();
    }
}
