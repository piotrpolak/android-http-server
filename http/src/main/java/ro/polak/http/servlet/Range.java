/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2017-2017
 **************************************************/
package ro.polak.http.servlet;

/**
 * Represents HTTP range.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201702
 */
public final class Range {

    private long from;
    private long to;

    public Range(final long from, final long to) {
        this.from = from;
        this.to = to;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }
}
