/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.gui;

import ro.polak.webserver.controller.Controller;
import ro.polak.webserver.controller.MainController;

/**
 * Server CLI interface along with a runner.
 */
public class ServerCliUi implements ServerGui {

    private Controller controller;

    /**
     * The main CLI runner method.
     *
     * @param args
     */
    public static void main(String[] args) {
        ServerGui gui = new ServerCliUi();
        gui.println("   __ __ ______ ______ ___    ____                         \n" +
                "  / // //_  __//_  __// _ \\  / __/___  ____ _  __ ___  ____\n" +
                " / _  /  / /    / /  / ___/ _\\ \\ / -_)/ __/| |/ // -_)/ __/\n" +
                "/_//_/  /_/    /_/  /_/    /___/ \\__//_/   |___/ \\__//_/   \n");
        gui.println("https://github.com/piotrpolak/android-http-server");
        gui.println("");
        MainController mainController = MainController.getInstance();
        mainController.setGui(gui);
        mainController.setContext(null);
        mainController.start();
    }

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
        controller.println(getClass(), "The server has stopped.");
    }

    @Override
    public void start() {
        controller.println(getClass(), "The server has started.");
    }
}
