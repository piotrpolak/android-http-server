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
import ro.polak.webserver.controller.Controller;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.error.HttpError503;
import ro.polak.webserver.resource.provider.AssetResourceProvider;
import ro.polak.webserver.resource.provider.FileResourceProvider;
import ro.polak.webserver.resource.provider.ResourceProvider;
import ro.polak.webserver.resource.provider.ServletResourceProvider;
import ro.polak.webserver.servlet.HttpRequestWrapper;
import ro.polak.webserver.servlet.HttpResponseWrapper;

/**
 * Web server main class.
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
    private Controller controller;
    private ServerConfig serverConfig;
    private ResourceProvider[] resourceProviders;

    /**
     * @param controller
     * @param serverSocket
     * @param serverConfig
     */
    public WebServer(Controller controller, ServerSocket serverSocket, ServerConfig serverConfig) {
        this.controller = controller;
        this.serverSocket = serverSocket;
        this.serverConfig = serverConfig;
        supportedMethods = new String[]{HttpRequestWrapper.METHOD_GET, HttpRequestWrapper.METHOD_POST, HttpRequestWrapper.METHOD_HEAD};
    }

    @Override
    public void run() {

        selectActiveResourceProviders();

        while (this.listen) {
            try {
                Socket socket = serverSocket.accept();
                // this.controller.println("Accepting connection from "+socket.getInetAddress().getHostAddress().toString());

                if (serverConfig.getMaxServerThreads() >= ServerThread.activeCount()) {
                    // If there are threads allowed to start
                    new ServerThread(socket, this).start();
                } else {
                    // 503 Service Unavailable HERE
                    (new HttpError503()).serve(HttpResponseWrapper.createFromSocket(socket));
                    socket.close();
                }
            } catch (IOException e) {
                if (listen) {
                    controller.println(this.getClass(), "Exception: " + e.getClass().getName() + " " + e.getMessage());
                }
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    private void selectActiveResourceProviders() {
        if (controller.getContext() != null) {
            resourceProviders = new ResourceProvider[]{
                    new FileResourceProvider(MainController.getInstance().getWebServer().getServerConfig().getDocumentRootPath()),
                    new AssetResourceProvider(),
                    new ServletResourceProvider()
            };
        } else {
            resourceProviders = new ResourceProvider[]{
                    new FileResourceProvider(MainController.getInstance().getWebServer().getServerConfig().getDocumentRootPath()),
                    new FileResourceProvider("./app/src/main/assets/public/"),
                    new ServletResourceProvider()
            };
        }
    }

    /**
     * Returns available resource providers
     *
     * @return
     */
    public ResourceProvider[] getResourceProviders() {
        return resourceProviders;
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
            controller.println(this.getClass(), "WARNING: DocumentRoot does not exist! PATH: " + serverConfig.getDocumentRootPath());
        }

        if (serverConfig.getMaxServerThreads() < 1) {
            controller.println(this.getClass(), "ERROR: MaxThreads should be greater or equal to 1! " + serverConfig.getMaxServerThreads() + " is given.");
            return false;
        }

        try {
            serverSocket.bind(new InetSocketAddress(serverConfig.getListenPort()));
        } catch (IOException e) {
            this.controller.println(this.getClass(), "ERROR: Unable to start server: unable to listen on port " + serverConfig.getListenPort() + " - " + e.getMessage());
            return false;
        }

        Utilities.clearDirectory(serverConfig.getTempPath());

        this.controller.println(this.getClass(), "Server has been started. Listening on port " + serverConfig.getListenPort());
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
        this.controller.println(this.getClass(), "Server has been stopped");
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
