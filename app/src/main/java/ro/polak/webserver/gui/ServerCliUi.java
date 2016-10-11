/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.gui;

import ro.polak.webserver.controller.Controller;
import ro.polak.webserver.controller.MainController;

/**
 * Server CLI interface
 */
public class ServerCliUi implements ServerGui {

    public static void main(String[] args) {
        MainController mainController = MainController.getInstance();
        mainController.setGui(new ServerCliUi());
        mainController.setContext(null);
        mainController.start();
    }

    Controller controller;

    @Override
    public void initialize(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void println(String text) {
        System.out.println(text);
    }

    @Override
    public void stop() {
        this.controller.println(this.getClass(), "The server has stopped.");
    }

    @Override
    public void start() {
        this.controller.println(this.getClass(), "The server has started.");
    }
}
