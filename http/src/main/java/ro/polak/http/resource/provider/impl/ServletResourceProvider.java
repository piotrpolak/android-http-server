/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.resource.provider.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.Headers;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.exception.UnexpectedSituationException;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpSessionWrapper;
import ro.polak.http.servlet.Servlet;
import ro.polak.http.servlet.ServletConfigWrapper;
import ro.polak.http.servlet.ServletContainer;
import ro.polak.http.servlet.ServletContext;
import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.servlet.UploadedFile;

/**
 * Servlet resource provider
 * <p/>
 * This provider enables the URLs to be interpreted by servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class ServletResourceProvider implements ResourceProvider {

    private static final Logger LOGGER = Logger.getLogger(ServletResourceProvider.class.getName());

    private final ServletContainer servletContainer;
    private final Set<ServletContextWrapper> servletContexts;

    /**
     * Default constructor.
     *
     * @param servletContainer
     * @param servletContexts
     */
    public ServletResourceProvider(final ServletContainer servletContainer,
                                   final Set<ServletContextWrapper> servletContexts) {
        this.servletContainer = servletContainer;
        this.servletContexts = servletContexts;
    }

    @Override
    public boolean canLoad(String path) {
        ServletContext servletContext = getResolvedContext(path);
        return servletContext != null && getResolvedServletMapping(servletContext, path) != null;
    }

    @Override
    public void load(String path, HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {

        // TODO Handling of ServletException should be improved

        ServletContextWrapper servletContext = getResolvedContext(path);
        ServletMapping servletMapping = getResolvedServletMapping(servletContext, path);

        try {
            ServletConfigWrapper servletConfig = new ServletConfigWrapper(servletContext);
            Servlet servlet = servletContainer.getForClass(servletMapping.getServletClass(), servletConfig);

            request.setServletContext(servletContext);
            response.setStatus(HttpServletResponse.STATUS_OK);
            servlet.service(request, response);

            terminate(request, response);
        } catch (ServletInitializationException e) {
            throw new UnexpectedSituationException(e);
        } catch (ServletException e) {
            throw new UnexpectedSituationException(e);
        }
    }

    //@Nullable
    private ServletMapping getResolvedServletMapping(ServletContext servletContext, String path) {
        Objects.requireNonNull(servletContext);
        for (ServletMapping servletMapping : servletContext.getServletMappings()) {
            String inContextPath = path.substring(servletContext.getContextPath().length());
            if (servletMapping.getUrlPattern().matcher(inContextPath).matches()) {
                return servletMapping;
            }
        }

        return null;
    }

    //@Nullable
    private ServletContextWrapper getResolvedContext(String path) {
        for (ServletContextWrapper servletContext : servletContexts) {
            if (path.startsWith(servletContext.getContextPath())) {
                return servletContext;
            }
        }
        return null;
    }

    /**
     * Terminates servlet. Sets all necessary headers, flushes content.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void terminate(HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {
        freeUploadedUnprocessedFiles(request.getUploadedFiles());

        HttpSessionWrapper session = (HttpSessionWrapper) request.getSession(false);
        if (session != null) {
            try {
                ((ServletContextWrapper) request.getServletContext()).handleSession(session, response);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Unable to persist session", e);
            }
        }

        if (!response.isCommitted()) {
            if (response.getContentType() == null) {
                response.setContentType("text/html");
            }

            response.getHeaders().setHeader(Headers.HEADER_CACHE_CONTROL, "no-cache");
            response.getHeaders().setHeader(Headers.HEADER_PRAGMA, "no-cache");
        }

        response.flush();
    }

    private void freeUploadedUnprocessedFiles(Collection<UploadedFile> uploadedFiles) {
        for (UploadedFile uploadedFile : uploadedFiles) {
            uploadedFile.destroy();
        }
    }
}
