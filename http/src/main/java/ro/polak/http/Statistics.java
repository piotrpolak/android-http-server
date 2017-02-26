/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http;

/**
 * Statistics class used to gather statistical data for the GUI
 *
 * @author Piotr Polak
 */
public class Statistics {

    private static long bytesSend = 0;
    private static long bytesReceived = 0;
    private static long requests = 0;
    private static long errors404 = 0;
    private static long errors500 = 0;

    /**
     * Increments bytes received counter.
     *
     * @param bytes
     */
    public static synchronized void addBytesReceived(long bytes) {
        bytesReceived += bytes;
    }

    /**
     * Increments bytes sent counter.
     *
     * @param bytes
     */
    public static synchronized void addBytesSent(long bytes) {
        bytesSend += bytes;
    }

    /**
     * Increments requests received counter.
     */
    public static synchronized void addRequest() {
        ++requests;
    }

    /**
     * Increments 404 errors counter.
     */
    public static synchronized void addError404() {
        ++errors404;
    }

    /**
     * Increments 500 errors counter.
     */
    public static synchronized void addError500() {
        ++errors500;
    }

    /**
     * Returns number of bytes sent.
     *
     * @return
     */
    public static synchronized long getBytesSent() {
        return bytesSend;
    }

    /**
     * Returns number of bytes received.
     *
     * @return
     */
    public static synchronized long getBytesReceived() {
        return bytesReceived;
    }

    /**
     * Returns number of requests handled.
     *
     * @return
     */
    public static synchronized long getRequests() {
        return requests;
    }

    /**
     * Returns number of 404 errors encountered.
     *
     * @return
     */
    public static synchronized long getError404s() {
        return errors404;
    }


    /**
     * Returns number of 500 errors encountered.
     *
     * @return
     */
    public static synchronized long getError500s() {
        return errors500;
    }
}
