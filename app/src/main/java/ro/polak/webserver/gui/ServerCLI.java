/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.gui;

import ro.polak.webserver.controller.IController;

public class ServerCLI implements IServerUI {

    /**
     * GUI initialization method
     *
     * @param controller
     */
    public void initialize(IController controller) {

    }

    /**
     * GUI print debug method
     *
     * @param text
     */
    public void println(String text) {
        System.out.println(text);
    }

    /**
     * GUI method called by controller on stop
     */
    public void stop() {
        this.println("The server has stopped.");
    }

    /**
     * GUI method called by controller on start
     */
    public void start() {
        this.println("The server has started.");
    }
}
