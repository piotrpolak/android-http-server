/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.errorhandler.HttpErrorHandler;
import ro.polak.http.errorhandler.impl.HttpError400Handler;
import ro.polak.http.errorhandler.impl.HttpError403Handler;
import ro.polak.http.errorhandler.impl.HttpError404Handler;
import ro.polak.http.errorhandler.impl.HttpError405Handler;
import ro.polak.http.errorhandler.impl.HttpError411Handler;
import ro.polak.http.errorhandler.impl.HttpError413Handler;
import ro.polak.http.errorhandler.impl.HttpError414Handler;
import ro.polak.http.errorhandler.impl.HttpError416Handler;
import ro.polak.http.errorhandler.impl.HttpError500Handler;
import ro.polak.http.errorhandler.impl.HttpError505Handler;
import ro.polak.http.exception.AccessDeniedException;
import ro.polak.http.exception.MethodNotAllowedException;
import ro.polak.http.exception.NotFoundException;
import ro.polak.http.protocol.exception.LengthRequiredException;
import ro.polak.http.protocol.exception.PayloadTooLargeProtocolException;
import ro.polak.http.protocol.exception.ProtocolException;
import ro.polak.http.protocol.exception.RangeNotSatisfiableProtocolException;
import ro.polak.http.protocol.exception.StatusLineTooLongProtocolException;
import ro.polak.http.protocol.exception.UnsupportedProtocolException;
import ro.polak.http.protocol.exception.UriTooLongProtocolException;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletRequestWrapperFactory;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.utilities.IOUtilities;

/**
 * Server thread.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class ServerRunnable implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ServerRunnable.class.getName());

    private final ServerConfig serverConfig;
    private final Socket socket;
    private final HttpServletRequestWrapperFactory requestFactory;

    /**
     * Default constructor.
     *
     * @param socket
     * @param serverConfig
     * @param requestFactory
     */
    public ServerRunnable(final Socket socket, final ServerConfig serverConfig, final HttpServletRequestWrapperFactory requestFactory) {
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

                String requestedPath = request.getRequestURI();

                if (isPathContainingIllegalCharacters(requestedPath)) {
                    throw new AccessDeniedException();
                }

                setDefaultResponseHeaders(request, response);

                validateRequest(request);

                ResourceProvider resourceProvider = getResourceProvider(requestedPath);
                if (resourceProvider == null) {
                    DirectoryIndexDescriptor directoryIndexDescriptor = loadDirectoryIndexResource(requestedPath);
                    if (directoryIndexDescriptor == null) {
                        throw new NotFoundException();
                    } else {
                        if (!isPathEndingWithSlash(requestedPath)) {
                            sendRedirectToDirectorySlashedPath(response, requestedPath);
                        } else {
                            directoryIndexDescriptor.getResourceProvider().load(
                                    directoryIndexDescriptor.getDirectoryPath(), request, response);
                        }
                    }
                } else {
                    resourceProvider.load(requestedPath, request, response);
                }
            } catch (RuntimeException e) {
                if (response != null) {
                    getHandler(e).serve(response);
                }

                throw e; // Make it logged by the main thread
            } finally {
                if (socket != null) {
                    IOUtilities.closeSilently(socket);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Encountered IOException when handling request {0}", new Object[]{
                    e.getMessage()
            });
        }
    }

    private boolean isPathEndingWithSlash(String originalPath) {
        return originalPath.substring(originalPath.length() - 1).equals("/");
    }

    private void sendRedirectToDirectorySlashedPath(HttpResponseWrapper response, String originalPath) throws IOException {
        response.setStatus(HttpServletResponse.STATUS_MOVED_PERMANENTLY);
        response.getHeaders().setHeader("Location", originalPath + "/");
        response.flush();
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
                Statistics.incrementError404();
                return new HttpError404Handler(serverConfig.getErrorDocument404Path());
            } else if (e instanceof MethodNotAllowedException) {
                return new HttpError405Handler(getAllowedMethods());
            } else {
                fallbackException = e;
            }
        } catch (Throwable handlingException) {
            fallbackException = handlingException;
        }

        Statistics.incrementError500();
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
        if (e instanceof UriTooLongProtocolException || e instanceof StatusLineTooLongProtocolException) {
            return new HttpError414Handler();
        } else if (e instanceof LengthRequiredException) {
            return new HttpError411Handler();
        } else if (e instanceof UnsupportedProtocolException) {
            return new HttpError505Handler();
        } else if (e instanceof PayloadTooLargeProtocolException) {
            return new HttpError413Handler();
        } else if (e instanceof RangeNotSatisfiableProtocolException) {
            return new HttpError416Handler();
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

    private DirectoryIndexDescriptor loadDirectoryIndexResource(String path) {
        String normalizedDirectoryPath = getNormalizedDirectoryPath(path);
        for (String index : serverConfig.getDirectoryIndex()) {
            String directoryIndexPath = normalizedDirectoryPath + index;
            ResourceProvider resourceProvider = getResourceProvider(directoryIndexPath);
            if (resourceProvider != null) {
                return new DirectoryIndexDescriptor(resourceProvider, directoryIndexPath);
            }
        }
        return null;
    }

    /**
     * Returns coma separated allowed methods
     *
     * @return
     */
    private String getAllowedMethods() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> supportedMethods = serverConfig.getSupportedMethods();
        for (int i = 0; i < supportedMethods.size(); i++) {
            stringBuilder.append(supportedMethods.get(i));
            if (i != supportedMethods.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }

    private ResourceProvider getResourceProvider(String path) {
        for (ResourceProvider resourceProvider : serverConfig.getResourceProviders()) {
            if (resourceProvider.canLoad(path)) {
                return resourceProvider;
            }
        }
        return null;
    }

    /**
     * Tells whether the given path contains illegal expressions.
     *
     * @param path
     * @return
     */
    private boolean isPathContainingIllegalCharacters(String path) {
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
            return path + "/";
        }
        return path;
    }

    /**
     * Throws exception in case of invalid request.
     *
     * @param request
     */
    private void validateRequest(HttpServletRequest request) {
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

    /**
     * Helper class describing directory index
     */
    private static class DirectoryIndexDescriptor {
        private ResourceProvider resourceProvider;
        private String directoryPath;

        public DirectoryIndexDescriptor(ResourceProvider resourceProvider, String indexFilePath) {
            this.resourceProvider = resourceProvider;
            this.directoryPath = indexFilePath;
        }

        public ResourceProvider getResourceProvider() {
            return resourceProvider;
        }

        public String getDirectoryPath() {
            return directoryPath;
        }
    }
}
