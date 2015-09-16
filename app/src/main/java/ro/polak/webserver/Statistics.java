/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

/**
 * Statistics class used to gather statistical data for the GUI
 *
 * @author Piotr Polak
 */
public class Statistics {

    // TODO Add comments

    private static long bytesSend = 0;
    private static long bytesReceived = 0;
    private static long requests = 0;
    private static long errors404 = 0;
    private static long errors500 = 0;

    public static synchronized void addBytesReceived(long bytes) {
        Statistics.bytesReceived += bytes;
    }

    public static synchronized void addBytesSend(long bytes) {
        Statistics.bytesSend += bytes;
    }

    public static synchronized void addRequest() {
        ++Statistics.requests;
    }

    public static synchronized void addError404() {
        ++Statistics.errors404;
    }

    public static synchronized void addError500() {
        ++Statistics.errors500;
    }

    public static long getBytesSend() {
        return Statistics.bytesSend;
    }

    public static long getBytesReceived() {
        return Statistics.bytesReceived;
    }

    public static long getRequests() {
        return Statistics.requests;
    }

    public static long getError404s() {
        return Statistics.errors404;
    }

    public static long getError500s() {
        return Statistics.errors500;
    }
}
