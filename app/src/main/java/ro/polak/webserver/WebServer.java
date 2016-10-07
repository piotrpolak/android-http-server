/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.controller.IController;
import ro.polak.webserver.error.HTTPError503;
import ro.polak.webserver.resourceloader.AssetResourceLoader;
import ro.polak.webserver.resourceloader.FileResourceLoader;
import ro.polak.webserver.resourceloader.IResourceLoader;
import ro.polak.webserver.resourceloader.ServletResourceLoader;
import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;

/**
 * Web server main class
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class WebServer extends Thread {

    // Some static info
    public static final String NAME = "AndroidHTTPServer";
    public static final String VERSION = "0.1.5-dev";
    public static final String SIGNATURE = NAME + "/" + VERSION;
    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
    private String[] supportedMethods;

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
        supportedMethods = new String[]{HTTPRequest.METHOD_GET, HTTPRequest.METHOD_POST, HTTPRequest.METHOD_HEAD};
    }

    @Override
    public void run() {
        while (this.listen) {
            try {
                Socket socket = serverSocket.accept();
                // this.controller.println("Accepting connection from "+socket.getInetAddress().getHostAddress().toString());

                if (serverConfig.getMaxServerThreads() >= ServerThread.activeCount()) {
                    // If there are threads allowed to start
                    new ServerThread(socket, this).start();
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
     * Returns available resource loaders
     *
     * @return
     */
    public IResourceLoader[] getResourceLoaders() {
        return new IResourceLoader[]{new FileResourceLoader(), new AssetResourceLoader(), new ServletResourceLoader()};
    }

    /**
     * Returns an array of supported HTTP methods
     *
     * @return
     */
    public String[] getSupportedMethods() {
        return supportedMethods;
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
