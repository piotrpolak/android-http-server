/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.controller;

import ro.polak.webserver.WebServer;

/**
 * Defines methods that must be implemented by the server controller
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201012
 */
public interface Controller {

    /**
     * Prints the log line into the specific output
     *
     * @param text
     */
    void println(String text);

    /**
     * Prints the log line into the specific output
     *
     * @param text
     */
    void println(Class c, String text);

    /**
     * Starts the server logic
     */
    void start();

    /**
     * Stops the server logic
     */
    void stop();

    /**
     * Returns the server thread
     *
     * @return
     */
    WebServer getWebServer();

    /**
     * Returns application context, this is mostly used for android applications
     *
     * @return
     */
    Object getContext();

    /**
     * Sets application context, this is mostly used for android applications
     *
     * @param context
     */
    void setContext(Object context);
}
