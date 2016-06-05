/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.gui;

import ro.polak.webserver.controller.IController;

/**
 * Server CLI interface
 */
public class ServerCLI implements IServerUI {

    @Override
    public void initialize(IController controller) {

    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }

    @Override
    public void stop() {
        this.println("The server has stopped.");
    }

    @Override
    public void start() {
        this.println("The server has started.");
    }
}
