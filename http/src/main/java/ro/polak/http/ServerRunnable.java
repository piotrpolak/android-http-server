/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.error.HttpErrorHandler;
import ro.polak.http.error.impl.HttpError400Handler;
import ro.polak.http.error.impl.HttpError403Handler;
import ro.polak.http.error.impl.HttpError404Handler;
import ro.polak.http.error.impl.HttpError405Handler;
import ro.polak.http.error.impl.HttpError414Handler;
import ro.polak.http.error.impl.HttpError500Handler;
import ro.polak.http.exception.AccessDeniedException;
import ro.polak.http.exception.MethodNotAllowedException;
import ro.polak.http.exception.NotFoundException;
import ro.polak.http.protocol.exception.ProtocolException;
import ro.polak.http.protocol.exception.StatusLineTooLongProtocolException;
import ro.polak.http.protocol.exception.UriTooLongProtocolException;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequest;
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
        HttpResponseWrapper response = null;

        try {
            try {
                response = HttpResponseWrapper.createFromSocket(socket);
                HttpRequestWrapper request = requestFactory.createFromSocket(socket);

                LOGGER.log(Level.INFO, "Handling request {0} {1}", new Object[]{
                        request.getMethod(), request.getRequestURI()
                });

                String path = request.getRequestURI();

                if (isPathIllegal(path)) {
                    throw new AccessDeniedException();
                }

                setDefaultResponseHeaders(request, response);

                validateRequest(request);

                boolean isResourceLoaded = loadResourceByPath(request, response, path);
                if (!isResourceLoaded) {
                    isResourceLoaded = loadDirectoryIndexResource(request, response, path);
                }
                if (!isResourceLoaded) {
                    throw new NotFoundException();
                }

            } catch (RuntimeException e) {
                if (response != null) {
                    getHandler(e).serve(response);
                }
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Encountered IOException when handling request {0}", new Object[]{
                    e.getMessage()
            });
        }
    }

    /**
     * Returns resolved handler for given exception.
     *
     * @param e
     * @return
     * @throws IOException
     */
    private HttpErrorHandler getHandler(RuntimeException e) throws IOException {
        Throwable fallbackException;

        try {
            if (e instanceof ProtocolException) {
                return getProtocolExceptionHandler((ProtocolException) e);
            } else if (e instanceof AccessDeniedException) {
                return new HttpError403Handler(serverConfig.getErrorDocument403Path());
            } else if (e instanceof NotFoundException) {
                return new HttpError404Handler(serverConfig.getErrorDocument404Path());
            } else if (e instanceof MethodNotAllowedException) {
                return new HttpError405Handler(getAllowedMethods());
            } else {
                fallbackException = e;
            }
        } catch (Throwable handlingException) {
            fallbackException = handlingException;
        }

        return new HttpError500Handler().setReason(fallbackException);
    }

    /**
     * Returns resolved handler for given ProtocolException.
     *
     * @param e
     * @return
     * @throws IOException
     */
    private HttpErrorHandler getProtocolExceptionHandler(ProtocolException e) throws IOException {
        if (e instanceof StatusLineTooLongProtocolException) {
            return new HttpError414Handler();
        } else if (e instanceof UriTooLongProtocolException) {
            return new HttpError414Handler();
        }

        return new HttpError400Handler();
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
            isKeepAlive = request.getHeaders().getHeader(Headers.HEADER_CONNECTION).equalsIgnoreCase("keep-alive");
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
     * Returns coma separated allowed methods
     *
     * @return
     */
    private String getAllowedMethods() {
        StringBuilder stringBuilder = new StringBuilder();
        String[] supportedMethods = serverConfig.getSupportedMethods();
        for (int i = 0; i < supportedMethods.length; i++) {
            stringBuilder.append(supportedMethods[i]);
            if (i != supportedMethods.length - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
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
        if (path.length() > 0 && path.charAt(path.length() - 1) != '/') {
            path += "/";
        }
        return path;
    }

    /**
     * Throws exception in case of invalid request.
     *
     * @param request
     */
    private void validateRequest(HttpRequest request) {
        if (!isMethodSupported(request.getMethod())) {
            throw new MethodNotAllowedException();
        }
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
