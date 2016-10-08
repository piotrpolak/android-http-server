/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.controller;

import java.io.IOException;
import java.net.ServerSocket;

import ro.polak.webserver.ServerConfig;
import ro.polak.webserver.WebServer;
import ro.polak.webserver.gui.*;

/**
 * The main controller of the server, can only be initialized as a singleton
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 2001012
 */
public class MainController implements Controller {

    private WebServer webServer;
    private ServerGui gui;
    private Object context;
    private static MainController instance;

    /**
     * Making the controller constructor private for singleton
     */
    private MainController() {

    }

    /**
     * Singleton method
     *
     * @return
     */
    public static MainController getInstance() {
        if (MainController.instance == null) {
            MainController.instance = new MainController();
        }

        return MainController.instance;
    }

    /**
     * Sets server GUI
     *
     * @param gui
     */
    public void setGui(ServerGui gui) {
        this.gui = gui;
    }

    /**
     * Prints the log line into the specific output
     *
     * @param text
     */
    public void println(String text) {
        // TODO Remove static call
        gui.println(WebServer.sdf.format(new java.util.Date()) + "  -  " + text);
    }

    /**
     * Starts the server logic
     */
    public void start() {
        gui.initialize(this);
        try {
            webServer = new WebServer(this, new ServerSocket(), new ServerConfig());
            webServer.startServer();
            gui.start();
        } catch (IOException e) {
            gui.stop();
        }
    }

    /**
     * Stops the server logic
     */
    public void stop() {
        if (webServer != null) {
            webServer.stopServer();
            webServer = null;
        }
        gui.stop();
    }

    /**
     * Returns the server thread
     *
     * @return
     */
    public WebServer getWebServer() {
        return webServer;
    }

    /**
     * Returns application context, this is mostly used for android applications
     *
     * @return
     */
    public Object getContext() {
        return context;
    }

    /**
     * Sets application context, this is mostly used for android applications
     *
     * @param context
     */
    public void setContext(Object context) {
        this.context = context;
    }
}
