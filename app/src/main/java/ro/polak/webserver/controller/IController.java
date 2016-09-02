/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.controller;

import ro.polak.webserver.WebServer;

import java.lang.Object;

/**
 * Defines methods that must be implemented by the server controller
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 2001012
 */
public interface IController {

    /**
     * Prints the log line into the specific output
     *
     * @param text
     */
    void println(String text);

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
    WebServer getServer();

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
