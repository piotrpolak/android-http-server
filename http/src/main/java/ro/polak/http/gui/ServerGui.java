/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http.gui;

/**
 * Defines methods that should be implemented by the server runner GUI (CLI, Swing, Android..).
 */
public interface ServerGui {

    /**
     * GUI method called by controller on stop.
     */
    void stop();

    /**
     * GUI method called by controller on start.
     */
    void start();
}
