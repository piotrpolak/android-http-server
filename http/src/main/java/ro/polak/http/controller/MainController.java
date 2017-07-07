/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.ServerConfigFactory;
import ro.polak.http.WebServer;
import ro.polak.http.gui.ServerGui;

/**
 * The main controller of the server, can only be initialized as a singleton
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201012
 */
public class MainController implements Controller {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private final ServerGui gui;
    private final ServerConfigFactory serverConfigFactory;

    private WebServer webServer;

    /**
     * Default constructor.
     */
    public MainController(final ServerConfigFactory serverConfigFactory, final ServerGui gui) {

        this.serverConfigFactory = serverConfigFactory;
        this.gui = gui;

        Thread.currentThread().setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                final String originalClass = ex.getStackTrace()[0].getClassName();
                Logger.getLogger(originalClass).log(Level.SEVERE, "Exception", ex);
            }
        });
    }

    @Override
    public WebServer getWebServer() {
        return webServer;
    }

    @Override
    public void start() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to create server socket ", e);
            return;
        }

        webServer = new WebServer(serverSocket, serverConfigFactory.getServerConfig());
        if (webServer.startServer()) {
            gui.start();
        }
    }

    @Override
    public void stop() {
        if (webServer != null) {
            webServer.stopServer();
            webServer = null;
        }
        gui.stop();
    }
}
