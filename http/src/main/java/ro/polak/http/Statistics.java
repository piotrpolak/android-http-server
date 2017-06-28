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
 *
 * The implementation relies on AtomicLong and it is thread safe.
 *
 * @author Piotr Polak
 */
public class Statistics {

    private static AtomicLong bytesSend = new AtomicLong();
    private static AtomicLong bytesReceived = new AtomicLong();
    private static AtomicLong requestsHandled = new AtomicLong();
    private static AtomicLong errors404 = new AtomicLong();
    private static AtomicLong errors500 = new AtomicLong();

    /**
     * Increments bytes received counter.
     *
     * @param bytes
     */
    public static synchronized void addBytesReceived(long bytes) {
        bytesReceived.addAndGet(bytes);
    }

    /**
     * Increments bytes sent counter.
     *
     * @param bytes
     */
    public static synchronized void addBytesSent(long bytes) {
        bytesSend.addAndGet(bytes);
    }

    /**
     * Increments requests handled counter.
     */
    public static synchronized void incrementRequestHandled() {
        requestsHandled.incrementAndGet();
    }

    /**
     * Increments 404 errors counter.
     */
    public static synchronized void incrementError404() {
        errors404.incrementAndGet();
    }

    /**
     * Increments 500 errors counter.
     */
    public static synchronized void incrementError500() {
        errors500.incrementAndGet();
    }

    /**
     * Returns number of bytes sent.
     *
     * @return
     */
    public static synchronized long getBytesSent() {
        return bytesSend.get();
    }

    /**
     * Returns number of bytes received.
     *
     * @return
     */
    public static synchronized long getBytesReceived() {
        return bytesReceived.get();
    }

    /**
     * Returns number of requestsHandled handled.
     *
     * @return
     */
    public static synchronized long getRequestsHandled() {
        return requestsHandled.get();
    }

    /**
     * Returns number of 404 errors encountered.
     *
     * @return
     */
    public static synchronized long getError404s() {
        return errors404.get();
    }


    /**
     * Returns number of 500 errors encountered.
     *
     * @return
     */
    public static synchronized long getError500s() {
        return errors500.get();
    }
}
