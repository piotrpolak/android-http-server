/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/
package ro.polak.http.protocol.parser.impl;

import java.util.List;

/**
 * Represents HTTP range.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public class Range {

    private long from;
    private long length;

    public Range() {
    }

    public Range(long from, long length) {
        this.from = from;
        this.length = length;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    /**
     * Computes total length of the provided ranges.
     *
     * @param ranges
     * @return
     */
    public static long getTotalLength(List<Range> ranges) {
        int totalLength = 0;
        for (Range range : ranges) {
            totalLength += range.getLength();
        }
        return totalLength;
    }

    /**
     * Tells whether the ranges are satisfiable for the given stream length
     *
     * @param ranges
     * @param streamLength
     * @return
     */
    public static boolean isSatisfiable(List<Range> ranges, long streamLength) {
        for (Range range : ranges) {
            if (range.getFrom() + range.getLength() > streamLength) {
                return false;
            }
        }

        return true;
    }
}
