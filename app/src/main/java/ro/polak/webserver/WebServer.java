/**************************************************
 * JavaLittleWebServer
 * Version 1.0 Beta
 * Date: 18/02/2008
 * <p/>
 * Author: Piotr Polak <piotr@polak.ro>
 * WWW: http://www.polak.ro
 **************************************************/
package ro.polak.webserver;

import ro.polak.webserver.controller.ControllerInterface;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class WebServer extends Thread {

    // Some static info
    public static final String SERVER_NAME = "JavaLittleWebServer";
    public static final String SERVER_VERSION = "1.5.0";
    public static final String SERVER_DATE = "12.09.2015";
    public static final String SERVER_SMALL_SIGNATURE = SERVER_NAME + "/" + SERVER_VERSION;
    public static final String SERVER_SIGNATURE = SERVER_NAME + "/" + SERVER_VERSION + " / " + SERVER_DATE;
    public static final String REQUEST_ALLOWED_METHODS = "GET PUT HEAD";
    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);

    static {
        WebServer.sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
    }

    private boolean listen = false;
    private ServerSocket serverSocket = null;
    private ControllerInterface controller = null;

    /**
     * Default constructor
     *
     * @param controller
     */
    public WebServer(ControllerInterface controller) {
        this.controller = controller;
        this.controller.println("Initializing WebServer.");
    }

    /**
     * The listen method
     */
    public void run() {
        Socket socket;

        // Listening
        while (this.listen) {
            try {
                // This blocks until the connection is done
                socket = serverSocket.accept();
                // this.controller.println("Accepting connection from "+socket.getInetAddress().getHostAddress().toString());

                if (JLWSConfig.MaxThreads >= ServerThread.activeCount()) {
                    // If there are threads allowed to start
                    new ServerThread(socket); // Creating new thread
                } else {
                    // 503 Service Unavailable HERE
                    HTTPError.serve503(socket);
                    socket.close();
                }
            } catch (IOException e) {
                if (this.listen) {
                    this.controller.println("ERROR: IO exception while accepting socket.");
                }
            }
        }

        this.listen = false;

        try {
            serverSocket.close();
        } catch (IOException e) {
            // Do nothing
        }
    }

    /**
     * Starts the web server
     */
    public boolean startServer() {
        this.listen = true;

        JLWSConfig.initialize();

        // Checking document root
        if (!(new File(JLWSConfig.DocumentRoot).isDirectory())) {
            this.controller.println("ERROR: DocumentRoot does not exist! PATH: "+ JLWSConfig.DocumentRoot);
            // return false;
        }

        // Getting the maximum number of server threads and veryfying
        if (JLWSConfig.MaxThreads < 1) {
            this.controller.println("ERROR: MaxThreads should be greater or equal to 1!");
            return false;
        }

        // Trying to create socket
        try {
            serverSocket = new ServerSocket(JLWSConfig.Listen);
        } catch (IOException e) {
            e.printStackTrace();
            this.controller.println("ERROR: Unable to start server: unable to listen on port " + JLWSConfig.Listen);
            return false;
        }

        this.controller.println("Server started. Listening on port " + JLWSConfig.Listen);
        this.start();
        return true;
    }

    public int getPort() {
        // TODO eliminate alias
        return JLWSConfig.Listen;
    }

    /**
     * Stops the web server
     */
    public void stopServer() {
        this.listen = false;
        try {
            serverSocket.close();
        } catch (Exception e) {
            // Can be IO or NULL
        }
        this.controller.println("Server stopped");
    }

    /**
     * Tells whether the server is running or no
     *
     * @return
     */
    public boolean isRunning() {
        return this.listen;
    }
}
