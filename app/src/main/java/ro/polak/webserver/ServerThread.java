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

import ro.polak.webserver.error.HTTPError403;
import ro.polak.webserver.error.HTTPError404;
import ro.polak.webserver.error.HTTPError405;
import ro.polak.webserver.resource.provider.ResourceProvider;
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

    /**
     * Default constructor.
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
            HTTPRequest request = HTTPRequest.createFromSocket(socket);
            HTTPResponse response = HTTPResponse.createFromSocket(socket);
            String path = request.getHeaders().getPath();

            if (isPathIllegal(path)) {
                (new HTTPError403()).serve(response);
                return;
            }

            setDefaultResponseHeaders(request, response);

            if (isMethodSupported(request.getHeaders().getMethod())) {
                boolean isResourceLoaded = loadResourceByPath(request, response, path);

                if (!isResourceLoaded) {
                    isResourceLoaded = loadDirectoryIndexResource(request, response, path);
                }

                if (!isResourceLoaded) {
                    (new HTTPError404()).serve(response);
                }

            } else {
                serveMethodNotAllowed(response);
            }

            socket.close();
        } catch (IOException e) {
        }
    }

    /**
     * Sets default response headers.
     *
     * @param request
     * @param response
     */
    private void setDefaultResponseHeaders(HTTPRequest request, HTTPResponse response) {
        response.setKeepAlive(request.isKeepAlive() && webServer.getServerConfig().isKeepAlive());
        response.getHeaders().setHeader(Headers.HEADER_SERVER, WebServer.SIGNATURE);
    }

    /**
     * Attempts to load resource by directory path.
     *
     * @param request
     * @param response
     * @param path
     * @return
     */
    private boolean loadDirectoryIndexResource(HTTPRequest request, HTTPResponse response, String path) {
        path = getNormalizedDirectoryPath(path);
        for (String index : webServer.getServerConfig().getDirectoryIndex()) {
            if (loadResourceByPath(request, response, path + index)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Server Method Not Allowed error page.
     *
     * @param response
     */
    private void serveMethodNotAllowed(HTTPResponse response) {
        StringBuilder sb = new StringBuilder();
        String[] supportedMethods = webServer.getSupportedMethods();
        for (int i = 0; i < supportedMethods.length; i++) {
            sb.append(supportedMethods[i]);
            if (i != supportedMethods.length - 1) {
                sb.append(", ");
            }
        }

        response.getHeaders().setHeader(Headers.HEADER_ALLOW, sb.toString());
        (new HTTPError405()).serve(response);
    }

    /**
     * Loads resource by path.
     *
     * @param request
     * @param response
     * @param path
     * @return
     */
    private boolean loadResourceByPath(HTTPRequest request, HTTPResponse response, String path) {
        ResourceProvider[] rl = webServer.getResourceProviders();
        for (int i = 0; i < rl.length; i++) {
            if (rl[i].load(path, request, response)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tells whether the given path contains illegal expressions.
     *
     * @param path
     * @return
     */
    private boolean isPathIllegal(String path) {
        return path == null || path.startsWith("../") || path.indexOf("/../") != -1;
    }

    /**
     * Makes sure the last character is a slash.
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

    /**
     * Tells whether the given HTTP method is supported.
     *
     * @param method
     * @return
     */
    private boolean isMethodSupported(String method) {
        for (String aMethod : webServer.getSupportedMethods()) {
            if (aMethod.equals(method)) {
                return true;
            }
        }
        return false;
    }
}
