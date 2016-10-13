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

import ro.polak.webserver.ServerConfig;
import ro.polak.webserver.WebServer;
import ro.polak.webserver.gui.ServerGui;

/**
 * The main controller of the server, can only be initialized as a singleton
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201012
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
        if (gui != null) {
            gui.println(WebServer.sdf.format(new java.util.Date()) + "  -  " + text);
        }
    }

    public void println(Class c, String text) {
        println(String.format("%1$-" + 50 + "s", c.getCanonicalName()) + "  -  " + text);
    }

    /**
     * Starts the server logic
     */
    public void start() {
        gui.initialize(this);
        try {
            String baseConfigPath;
            if (getContext() != null) {
                baseConfigPath = Environment.getExternalStorageDirectory() + "/httpd/";
            } else {
                baseConfigPath = "./app/src/main/assets/conf/";
            }

            String tempPath = System.getProperty("java.io.tmpdir");

            println(getClass(), "Temp directory: " + tempPath);

            webServer = new WebServer(this, new ServerSocket(), new ServerConfig(baseConfigPath, tempPath));
            if (webServer.startServer()) {
                gui.start();
            }
        } catch (IOException e) {
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
