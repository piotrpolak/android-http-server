/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.error.HttpError400;
import ro.polak.http.error.HttpError403;
import ro.polak.http.error.HttpError404;
import ro.polak.http.error.HttpError405;
import ro.polak.http.error.HttpError414;
import ro.polak.http.protocol.exception.ProtocolException;
import ro.polak.http.protocol.exception.StatusLineTooLongProtocolException;
import ro.polak.http.protocol.exception.UriTooLongProtocolException;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpRequestWrapperFactory;
import ro.polak.http.servlet.HttpResponseWrapper;

/**
 * Server thread.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class ServerRunnable implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ServerRunnable.class.getName());

    private ServerConfig serverConfig;
    private Socket socket;
    private HttpRequestWrapperFactory requestFactory;

    /**
     * Default constructor.
     *
     * @param socket
     * @param serverConfig
     * @param requestFactory
     */
    public ServerRunnable(Socket socket, final ServerConfig serverConfig, final HttpRequestWrapperFactory requestFactory) {
        this.socket = socket;
        this.serverConfig = serverConfig;
        this.requestFactory = requestFactory;
    }

    @Override
    public void run() {
        try {

            HttpResponseWrapper response = HttpResponseWrapper.createFromSocket(socket);

            HttpRequestWrapper request;
            try {
                request = requestFactory.createFromSocket(socket);
            } catch (ProtocolException e) {
                handleProtocolException(e, response);
                socket.close();
                return;
            }

            LOGGER.log(Level.INFO, "Handling request {0} {1}", new Object[]{
                    request.getMethod(), request.getRequestURI()
            });

            String path = request.getRequestURI();

            if (isPathIllegal(path)) {
                (new HttpError403(serverConfig.getErrorDocument403Path())).serve(response);
                socket.close();
                return;
            }

            setDefaultResponseHeaders(request, response);

            if (isMethodSupported(request.getMethod())) {
                boolean isResourceLoaded = loadResourceByPath(request, response, path);
                if (!isResourceLoaded) {
                    isResourceLoaded = loadDirectoryIndexResource(request, response, path);
                }
                if (!isResourceLoaded) {
                    (new HttpError404(serverConfig.getErrorDocument404Path())).serve(response);
                    socket.close();
                    return;
                }
            } else {
                serveMethodNotAllowed(response);
            }

            socket.close();
        } catch (IOException e) {
        }
    }

    /**
     * Handles protocol exception. Writes specified response.
     *
     * @param e
     * @param response
     * @throws IOException
     */
    private void handleProtocolException(ProtocolException e, HttpResponseWrapper response) throws IOException {
        if (e instanceof StatusLineTooLongProtocolException) {
            (new HttpError414()).serve(response);
        } else if (e instanceof UriTooLongProtocolException) {
            (new HttpError414()).serve(response);
        } else {
            (new HttpError400()).serve(response);
        }
    }

    /**
     * Sets default response headers.
     *
     * @param request
     * @param response
     */
    private void setDefaultResponseHeaders(HttpRequestWrapper request, HttpResponseWrapper response) {
        boolean isKeepAlive = false;
        if (request.getHeaders().containsHeader(Headers.HEADER_CONNECTION)) {
            isKeepAlive = request.getHeaders().getHeader(Headers.HEADER_CONNECTION).toLowerCase().equals("keep-alive");
        }

        response.setKeepAlive(isKeepAlive && serverConfig.isKeepAlive());
        response.getHeaders().setHeader(Headers.HEADER_SERVER, WebServer.SIGNATURE);
    }

    /**
     * Attempts to load resource by directory path.
     *
     * @param request
     * @param response
     * @param path
     * @return
     * @throws IOException
     */
    private boolean loadDirectoryIndexResource(HttpRequestWrapper request, HttpResponseWrapper response, String path) throws IOException {
        path = getNormalizedDirectoryPath(path);
        for (String index : serverConfig.getDirectoryIndex()) {
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
     * @throws IOException
     */
    private void serveMethodNotAllowed(HttpResponseWrapper response) throws IOException {
        StringBuilder sb = new StringBuilder();
        String[] supportedMethods = serverConfig.getSupportedMethods();
        for (int i = 0; i < supportedMethods.length; i++) {
            sb.append(supportedMethods[i]);
            if (i != supportedMethods.length - 1) {
                sb.append(", ");
            }
        }

        response.getHeaders().setHeader(Headers.HEADER_ALLOW, sb.toString());
        (new HttpError405()).serve(response);
    }

    /**
     * Loads resource by path.
     *
     * @param request
     * @param response
     * @param path
     * @return
     * @throws IOException
     */
    private boolean loadResourceByPath(HttpRequestWrapper request, HttpResponseWrapper response, String path) throws IOException {
        ResourceProvider[] rl = serverConfig.getResourceProviders();
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
        for (String aMethod : serverConfig.getSupportedMethods()) {
            if (aMethod.equals(method)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns associated socket.
     *
     * @return
     */
    protected Socket getSocket() {
        return socket;
    }
}
