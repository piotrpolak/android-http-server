/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.controller.IController;
import ro.polak.webserver.error.HTTPError503;
import ro.polak.webserver.servlet.HTTPResponse;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Web server main class
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class WebServer extends Thread {

    // Some static info
    public static final String SERVER_NAME = "AndroidHTTPServer";
    public static final String SERVER_VERSION = "0.1.5-dev";
    public static final String SERVER_DATE = "03.09.2016";
    public static final String SERVER_SMALL_SIGNATURE = SERVER_NAME + "/" + SERVER_VERSION;
    public static final String SERVER_SIGNATURE = SERVER_NAME + "/" + SERVER_VERSION + " / " + SERVER_DATE;
    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);

    static {
        WebServer.sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
    }

    private boolean listen;
    private ServerSocket serverSocket;
    private IController controller;

    private ServerConfig serverConfig;

    /**
     * @param controller
     * @param serverSocket
     * @param serverConfig
     */
    public WebServer(IController controller, ServerSocket serverSocket, ServerConfig serverConfig) {
        this.controller = controller;
        this.serverSocket = serverSocket;
        this.serverConfig = serverConfig;
    }

    @Override
    public void run() {
        while (this.listen) {
            try {
                Socket socket = serverSocket.accept();
                // this.controller.println("Accepting connection from "+socket.getInetAddress().getHostAddress().toString());

                if (serverConfig.getMaxServerThreads() >= ServerThread.activeCount()) {
                    // If there are threads allowed to start
                    new ServerThread(socket, this); // Creating new thread
                } else {
                    // 503 Service Unavailable HERE
                    (new HTTPError503()).serve(HTTPResponse.createFromSocket(socket));
                    socket.close();
                }
            } catch (IOException e) {
                if (listen) {
                    controller.println("Exception: " + e.getClass().getName() + " " + e.getMessage());
                }
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    /**
     * Starts the web server
     */
    public boolean startServer() {
        listen = true;

        if (!(new File(serverConfig.getDocumentRootPath()).isDirectory())) {
            controller.println("WARNING: DocumentRoot does not exist! PATH: " + serverConfig.getDocumentRootPath());
        }

        if (serverConfig.getMaxServerThreads() < 1) {
            controller.println("ERROR: MaxThreads should be greater or equal to 1! " + serverConfig.getMaxServerThreads() + " is given.");
            return false;
        }

        try {
            serverSocket.bind(new InetSocketAddress(serverConfig.getListenPort()));
        } catch (IOException e) {
            this.controller.println("ERROR: Unable to start server: unable to listen on port " + serverConfig.getListenPort() + "/" + e.getMessage());
            return false;
        }

        Utilities.clearDirectory(serverConfig.getTempPath());

        this.controller.println("Server has been started. Listening on port " + serverConfig.getListenPort());
        this.start();
        return true;
    }

    /**
     * Stops the web server
     */
    public void stopServer() {
        listen = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
        this.controller.println("Server has been stopped");
    }

    /**
     * Tells whether the server is running or not
     *
     * @return
     */
    public boolean isRunning() {
        return listen;
    }

    /**
     * Returns server config
     *
     * @return
     */
    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
