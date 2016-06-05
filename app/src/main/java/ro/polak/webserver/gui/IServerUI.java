/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.gui;

import ro.polak.webserver.controller.IController;

/**
 * Defines methods that should be implemented by the server runner GUI (CLI, Swing, Android..)
 */
public interface IServerUI {

    /**
     * GUI initialization method
     *
     * @param controller
     */
    void initialize(IController controller);

    /**
     * GUI print debug method
     *
     * @param text
     */
    void println(String text);

    /**
     * GUI method called by controller on stop
     */
    void stop();

    /**
     * GUI method called by controller on start
     */
    void start();
}
