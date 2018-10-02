/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.http.controller;

import ro.polak.http.WebServer;

/**
 * Defines methods that must be implemented by the server controller.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201012
 */
public interface Controller {

    /**
     * Starts the server logic.
     */
    void start() throws IllegalStateException;

    /**
     * Stops the server logic.
     */
    void stop() throws IllegalStateException;

    /**
     * Returns web server instance.
     *
     * @return
     */
    WebServer getWebServer();
}
