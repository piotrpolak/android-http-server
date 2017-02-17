/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/

package ro.polak.http.protocol.serializer.impl;

import ro.polak.http.Headers;
import ro.polak.http.RangePartHeader;
import ro.polak.http.protocol.serializer.Serializer;

/**
 * Serializes range part header to string representation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public class RangePartHeaderSerializer implements Serializer<RangePartHeader> {

    private static final String NEW_LINE = "\r\n";

    @Override
    public String serialize(RangePartHeader input) {

        // TODO Unit test

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--").append(input.getBoundary()).append(NEW_LINE)

                .append(Headers.HEADER_CONTENT_TYPE).append(": ")
                .append(input.getContentType()).append(NEW_LINE)

                .append(Headers.HEADER_CONTENT_RANGE).append(": ")
                .append(input.getRange().getFrom()).append("-").append(input.getRange().getTo())
                .append("/").append(input.getTotalLength()).append(NEW_LINE)
                .append(NEW_LINE);

        return stringBuilder.toString();
    }
}
