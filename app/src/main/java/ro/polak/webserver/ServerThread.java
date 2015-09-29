/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver;

import java.net.Socket;

import ro.polak.webserver.error.HTTPError404;
import ro.polak.webserver.error.HTTPError405;
import ro.polak.webserver.resourceloader.AssetResourceLoader;
import ro.polak.webserver.resourceloader.FileResourceLoader;
import ro.polak.webserver.resourceloader.IResourceLoader;
import ro.polak.webserver.resourceloader.ServletResourceLoader;
import ro.polak.webserver.servlet.*;

/**
 * Server thread
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 201802
 */
public class ServerThread extends Thread {

    private Socket socket;
    private WebServer webServer;

    private static IResourceLoader[] rl;

    static {
        // The list of all allowed resource loaders
        rl = new IResourceLoader[]{new FileResourceLoader(), new AssetResourceLoader(), new ServletResourceLoader()};
    }

    /**
     * Default constructor
     *
     * @param socket
     */
    public ServerThread(Socket socket, WebServer webServer) {
        this.socket = socket;
        this.webServer = webServer;
        this.start();
    }

    /**
     * This method is called to handle request in a new thread
     */
    public void run() {

        // Creating new request and response objects
        HTTPRequest request = HTTPRequest.createFromSocket(socket);
        HTTPResponse response = new HTTPResponse(socket);

        // Checking the requested URI, blocking illegal paths
        if (request.getHeaders().getURI() == null || request.getHeaders().getURI().startsWith("../") || request.getHeaders().getURI().indexOf("/../") != -1) {
            try {
                response.close();
                response.close();
            } catch (Exception e) {
            }
            return;
        }

        // Setting keep alive header
        if (request.isKeepAlive() && webServer.getServerConfig().isKeepAlive()) {
            response.setKeepAlive(true);
        } else {
            response.setKeepAlive(false);
        }

        // Setting signature header
        response.setHeader("Server", WebServer.SERVER_SMALL_SIGNATURE);

        // Checking allowed method
        if (request.getHeaders().getMethod().equals("GET") || request.getHeaders().getMethod().equals("POST") || request.getHeaders().getMethod().equals("HEAD")) {

            // Coping the value
            String uri = request.getHeaders().getURI();

            // This variable becomes true when one of the resource loaders manage to load a resource
            boolean resourceSuccessfullyLoaded = false;

            // Trying to load a resource using each of the resource loaders
            for (int ri = 0; ri < rl.length; ri++) {
                if (rl[ri].load(uri, request, response)) {
                    resourceSuccessfullyLoaded = true;
                    break;
                }
            }

            // Trying to load directory index
            if (!resourceSuccessfullyLoaded) {
                for (int i = 0; i < webServer.getServerConfig().getDirectoryIndex().size(); i++) {

                    // Appending an extra slash
                    if (uri.length() > 0) {
                        if (uri.charAt(uri.length() - 1) != '/') {
                            uri += "/";
                        }
                    }

                    // Getting the current directory index
                    String directoryIndex = webServer.getServerConfig().getDirectoryIndex().elementAt(i);

                    // Trying to load a resource using each of the resource loaders
                    for (int ri = 0; ri < rl.length; ri++) {
                        if (rl[ri].load(uri + directoryIndex, request, response)) {
                            resourceSuccessfullyLoaded = true;
                            break;
                        }
                    }

                    // Breaking the parent loop in case one of the loaders managed to serve a resource
                    if (resourceSuccessfullyLoaded) {
                        break;
                    }
                }
            }

            // Serving 404 error
            if (!resourceSuccessfullyLoaded) {
                (new HTTPError404()).serve(response);
            }

        } else {
            // Method not allowed
            (new HTTPError405()).serve(response);
        }

        // Closing socket and closing the response
        try {
            this.socket.getInputStream().close();
        } catch (Exception e) {
        }

        try {
            response.close();
        } catch (Exception e) {
        }

        // Cleanup
        response = null;
        request = null;
        this.socket = null;
    }
}
