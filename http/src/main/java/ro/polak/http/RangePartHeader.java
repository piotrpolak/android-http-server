/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/
package ro.polak.http;

import ro.polak.http.servlet.Range;

/**
 * Represents range part header.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public final class RangePartHeader {

    private final Range range;
    private final String boundary;
    private final String contentType;
    private final long totalLength;

    public RangePartHeader(final Range range,
                           final String boundary,
                           final String contentType,
                           final long totalLength) {
        this.range = range;
        this.boundary = boundary;
        this.contentType = contentType;
        this.totalLength = totalLength;
    }

    public Range getRange() {
        return range;
    }

    public String getBoundary() {
        return boundary;
    }

    public String getContentType() {
        return contentType;
    }

    public long getTotalLength() {
        return totalLength;
    }
}
