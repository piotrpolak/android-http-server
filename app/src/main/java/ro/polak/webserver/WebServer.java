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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.error.HttpError503;
import ro.polak.webserver.servlet.HttpRequestWrapperFactory;
import ro.polak.webserver.servlet.HttpResponseWrapper;

/**
 * Web server main class.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class WebServer extends Thread {

    private static final Logger LOGGER = Logger.getLogger(WebServer.class.getName());

    public static final String NAME = "AndroidHTTPServer";
    public static final String VERSION = "0.1.5-dev";
    public static final String SIGNATURE = NAME + "/" + VERSION;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);

    static {
        WebServer.sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
    }

    private boolean listen;
    private ServerSocket serverSocket;
    private ServerConfig serverConfig;

    /**
     * @param serverSocket
     * @param serverConfig
     */
    public WebServer(ServerSocket serverSocket, ServerConfig serverConfig) {
        this.serverSocket = serverSocket;
        this.serverConfig = serverConfig;
    }

    @Override
    public void run() {
        ThreadPoolExecutor executorPool = new ThreadPoolExecutor(1, serverConfig.getMaxServerThreads(),
                20, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(100),
                Executors.defaultThreadFactory(),
                new ServiceUnavailableHandler()
        );

        HttpRequestWrapperFactory requestWrapperFactory = new HttpRequestWrapperFactory(getServerConfig().getTempPath());

        while (listen) {
            try {
                Socket socket = serverSocket.accept();
                executorPool.execute(new ServerRunnable(socket, getServerConfig(), requestWrapperFactory));
            } catch (IOException e) {
                if (listen) {
                    LOGGER.log(Level.SEVERE, "Communication error", e);
                }
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
        }

        executorPool.shutdown();
    }

    /**
     * Starts the web server
     */
    public boolean startServer() {
        listen = true;

        if (!(new File(serverConfig.getDocumentRootPath()).isDirectory())) {
            LOGGER.log(Level.WARNING, "DocumentRoot does not exist! PATH {0}", new Object[]{
                    serverConfig.getDocumentRootPath()
            });
        }

        if (serverConfig.getMaxServerThreads() < 1) {
            LOGGER.log(Level.SEVERE, "MaxThreads should be greater or equal to 1! {0} is given.",
                    new Object[]{serverConfig.getMaxServerThreads()});
            return false;
        }

        try {
            serverSocket.bind(new InetSocketAddress(serverConfig.getListenPort()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to start server: unable to listen on port " +
                    serverConfig.getListenPort(), e);
            return false;
        }

        Utilities.clearDirectory(serverConfig.getTempPath());

        LOGGER.log(Level.INFO, "Server has been started. Listening on port {0}", new Object[]{
                serverConfig.getListenPort()
        });
        start();
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
        LOGGER.info("Server has been stopped.");
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

    /**
     * ServiceUnavailableHandler is responsible for sending 503 error pages when there is more space
     * in the runnable queue. To test this class you have to limit the number of available threads
     * and queue size to 1 and then to try open multiple connections at the same time.
     */
    private static class ServiceUnavailableHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof ServerRunnable) {
                Socket socket = ((ServerRunnable) r).getSocket();
                try {
                    (new HttpError503()).serve(HttpResponseWrapper.createFromSocket(socket));
                } catch (IOException e) {
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
