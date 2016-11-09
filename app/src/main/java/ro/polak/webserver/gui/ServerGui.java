/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.gui;

import ro.polak.webserver.controller.Controller;

/**
 * Defines methods that should be implemented by the server runner GUI (CLI, Swing, Android..)
 */
public interface ServerGui {

    /**
     * GUI initialization method
     *
     * @param controller
     */
    void initialize(Controller controller);

    /**
     * GUI method called by controller on stop
     */
    void stop();

    /**
     * GUI method called by controller on start
     */
    void start();
}
