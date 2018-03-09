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

import javax.net.ServerSocketFactory;

import ro.polak.http.configuration.ServerConfigFactory;
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
    private final ServerSocketFactory serverSocketFactory;

    private WebServer webServer;

    /**
     * Default constructor.
     */
    public MainController(final ServerConfigFactory serverConfigFactory,
                          final ServerSocketFactory serverSocketFactory,
                          final ServerGui gui) {

        this.serverConfigFactory = serverConfigFactory;
        this.serverSocketFactory = serverSocketFactory;
        this.gui = gui;

        Thread.currentThread().setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
    }

    @Override
    public WebServer getWebServer() {
        return webServer;
    }

    @Override
    public void start() throws IllegalStateException {
        if (webServer != null) {
            throw new IllegalStateException("Webserver already started!");
        }
        ServerSocket serverSocket;
        try {
            serverSocket = serverSocketFactory.createServerSocket();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to create server socket ", e);
            return;
        }

        webServer = new WebServer(serverSocket, serverConfigFactory.getServerConfig());
        if (webServer.startServer()) {
            gui.start();
        } else {
            webServer = null;
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        if (webServer == null) {
            throw new IllegalStateException("Webserver not started!");
        }

        webServer.stopServer();
        webServer = null;
        gui.stop();
    }


    public static class LoggingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            final String originalClass = ex.getStackTrace()[0].getClassName();
            Logger.getLogger(originalClass).log(Level.SEVERE, "Exception", ex);
        }
    }
}
