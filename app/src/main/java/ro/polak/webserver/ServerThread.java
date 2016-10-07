/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.net.Socket;

import ro.polak.webserver.error.HTTPError404;
import ro.polak.webserver.error.HTTPError405;
import ro.polak.webserver.resourceloader.AssetResourceLoader;
import ro.polak.webserver.resourceloader.FileResourceLoader;
import ro.polak.webserver.resourceloader.IResourceLoader;
import ro.polak.webserver.resourceloader.ServletResourceLoader;
import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;

/**
 * Server thread
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class ServerThread extends Thread {

    private Socket socket;
    private WebServer webServer;

    private static IResourceLoader[] rl = new IResourceLoader[]{new FileResourceLoader(), new AssetResourceLoader(), new ServletResourceLoader()};
    private static String[] supportedMethods = {HTTPRequest.METHOD_GET, HTTPRequest.METHOD_POST, HTTPRequest.METHOD_HEAD};

    // Dynamically compute the value of Allow header
    private static String allowHeaderValue = "";

    static {
        for (int i = 0; i < supportedMethods.length; i++) {
            allowHeaderValue += supportedMethods[i];
            if (i != supportedMethods.length - 1) {
                allowHeaderValue += ", ";
            }
        }
    }

    /**
     * Default constructor
     *
     * @param socket
     */
    public ServerThread(Socket socket, WebServer webServer) {
        this.socket = socket;
        this.webServer = webServer;
    }

    @Override
    public void run() {
        try {
            // Creating new request and response objects
            HTTPRequest request = HTTPRequest.createFromSocket(socket);
            HTTPResponse response = HTTPResponse.createFromSocket(socket);

            String path = request.getHeaders().getPath();

            // Checking the requested URI, blocking illegal paths
            if (isIllegalPath(path)) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
                return;
            }

            response.setKeepAlive(request.isKeepAlive() && webServer.getServerConfig().isKeepAlive());
            response.getHeaders().setHeader(Headers.HEADER_SERVER, WebServer.SIGNATURE);

            if (isMethodSupported(request.getHeaders().getMethod())) {
                // This variable becomes true when one of the resource loaders manage to load a resource
                boolean isLoaded = loadResourceByPath(request, response, path);

                // Trying to load directory index
                if (!isLoaded) {
                    path = getNormalizedDirectoryPath(path);
                    for (int i = 0; i < webServer.getServerConfig().getDirectoryIndex().size(); i++) {
                        String directoryIndex = (String) webServer.getServerConfig().getDirectoryIndex().get(i);
                        isLoaded = loadResourceByPath(request, response, path + directoryIndex);

                        // Breaking the parent loop in case one of the loaders managed to serve a resource
                        if (isLoaded) {
                            break;
                        }
                    }
                }

                // Serving 404 error
                if (!isLoaded) {
                    (new HTTPError404()).serve(response);
                }

            } else {
                // Method not allowed
                response.getHeaders().setHeader(Headers.HEADER_ALLOW, allowHeaderValue);
                (new HTTPError405()).serve(response);
            }
        } catch (IOException e) {
        }

        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    private boolean loadResourceByPath(HTTPRequest request, HTTPResponse response, String path) {
        for (int ri = 0; ri < rl.length; ri++) {
            if (rl[ri].load(path, request, response)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIllegalPath(String path) {
        return path == null || path.startsWith("../") || path.indexOf("/../") != -1;
    }

    /**
     * Makes sure the last character is a slash
     *
     * @param path
     * @return
     */
    @NonNull
    private String getNormalizedDirectoryPath(String path) {
        if (path.length() > 0) {
            if (path.charAt(path.length() - 1) != '/') {
                path += "/";
            }
        }
        return path;
    }

    private boolean isMethodSupported(String method) {
        for (int i = 0; i < supportedMethods.length; i++) {
            if (supportedMethods[i].equals(method)) {
                return true;
            }
        }
        return false;
    }
}
