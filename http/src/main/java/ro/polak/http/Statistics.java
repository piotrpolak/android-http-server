/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Statistics class used to gather statistical data for the GUI.
 * <p>
 * The implementation relies on AtomicLong and it is thread safe.
 *
 * @author Piotr Polak
 */
public final class Statistics {

    private static final AtomicLong BYTES_SEND = new AtomicLong();
    private static final AtomicLong BYTES_RECEIVED = new AtomicLong();
    private static final AtomicLong REQUESTS_HANDLED = new AtomicLong();
    private static final AtomicLong ERRORS_404 = new AtomicLong();
    private static final AtomicLong ERRORS_500 = new AtomicLong();

    private Statistics() {
    }

    /**
     * Resets all statistical values.
     */
    public static void reset() {
        BYTES_SEND.lazySet(0);
        BYTES_RECEIVED.lazySet(0);
        REQUESTS_HANDLED.lazySet(0);
        ERRORS_404.lazySet(0);
        ERRORS_500.lazySet(0);
    }

    /**
     * Increments bytes received counter.
     *
     * @param bytes
     */
    public static void addBytesReceived(final long bytes) {
        BYTES_RECEIVED.addAndGet(bytes);
    }

    /**
     * Increments bytes sent counter.
     *
     * @param bytes
     */
    public static void addBytesSent(final long bytes) {
        BYTES_SEND.addAndGet(bytes);
    }

    /**
     * Increments requests handled counter.
     */
    public static void incrementRequestHandled() {
        REQUESTS_HANDLED.incrementAndGet();
    }

    /**
     * Increments 404 errors counter.
     */
    public static void incrementError404() {
        ERRORS_404.incrementAndGet();
    }

    /**
     * Increments 500 errors counter.
     */
    public static void incrementError500() {
        ERRORS_500.incrementAndGet();
    }

    /**
     * Returns number of bytes sent.
     *
     * @return
     */
    public static long getBytesSent() {
        return BYTES_SEND.get();
    }

    /**
     * Returns number of bytes received.
     *
     * @return
     */
    public static long getBytesReceived() {
        return BYTES_RECEIVED.get();
    }

    /**
     * Returns number of REQUESTS_HANDLED handled.
     *
     * @return
     */
    public static long getRequestsHandled() {
        return REQUESTS_HANDLED.get();
    }

    /**
     * Returns number of 404 errors encountered.
     *
     * @return
     */
    public static long getError404s() {
        return ERRORS_404.get();
    }


    /**
     * Returns number of 500 errors encountered.
     *
     * @return
     */
    public static long getError500s() {
        return ERRORS_500.get();
    }
}
