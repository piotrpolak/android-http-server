/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.controller;

import android.os.Environment;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.webserver.ServerConfig;
import ro.polak.webserver.WebServer;
import ro.polak.webserver.gui.ServerGui;
import ro.polak.webserver.impl.ServerConfigImpl;

/**
 * The main controller of the server, can only be initialized as a singleton
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201012
 */
public class MainController implements Controller {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    private WebServer webServer;
    private ServerGui gui;
    private Object context;
    private static MainController instance;

    /**
     * Making the controller constructor private for singleton
     */
    private MainController() {
        Thread.currentThread().setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                final String originalClass = ex.getStackTrace()[0].getClassName();
                Logger.getLogger(originalClass).log(Level.SEVERE, "Exception", ex);
            }
        });
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

    @Override
    public void start() {
        gui.initialize(this);
        try {
            String baseConfigPath;
            if (getContext() != null) {
                // On Android
                baseConfigPath = Environment.getExternalStorageDirectory() + "/httpd/";
            } else {
                // On desktop
                baseConfigPath = "./app/src/main/assets/conf/";
            }

            ServerConfig serverConfig;
            try {
                serverConfig = ServerConfigImpl.createFromPath(baseConfigPath, System.getProperty("java.io.tmpdir"));
            } catch (IOException e) {
                LOGGER.warning("Unable to read server config. Using the default configuration.");
                serverConfig = new ServerConfigImpl();
            }

            webServer = new WebServer(this, new ServerSocket(), serverConfig);
            if (webServer.startServer()) {
                gui.start();
            }
        } catch (IOException e) {
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

    @Override
    public WebServer getWebServer() {
        return webServer;
    }

    @Override
    public Object getContext() {
        return context;
    }

    @Override
    public void setContext(Object context) {
        this.context = context;
    }
}
