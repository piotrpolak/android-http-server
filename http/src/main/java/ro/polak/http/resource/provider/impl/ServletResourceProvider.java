/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.resource.provider.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.Headers;
import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.exception.UnexpectedSituationException;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpSessionWrapper;
import ro.polak.http.servlet.Servlet;
import ro.polak.http.servlet.ServletConfig;
import ro.polak.http.servlet.ServletConfigWrapper;
import ro.polak.http.servlet.ServletContainer;
import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.servlet.ServletPathTranslator;
import ro.polak.http.servlet.UploadedFile;
import ro.polak.http.servlet.loader.ServletLoader;
import ro.polak.http.utilities.Utilities;

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
    private final ServletLoader servletLoader;
    private final ServletPathTranslator servletPathTranslator;
    private final ServletContainer servletContainer;
    private final ServletContextWrapper servletContext;
    private final ServletConfig servletConfig;
    private final String servletMappedExtension;

    /**
     * Default constructor.
     *
     * @param servletLoader
     * @param servletPathTranslator
     * @param servletContainer
     * @param servletContext
     * @param servletMappedExtension
     */
    public ServletResourceProvider(final ServletLoader servletLoader,
                                   final ServletPathTranslator servletPathTranslator,
                                   final ServletContainer servletContainer,
                                   final ServletContextWrapper servletContext,
                                   final String servletMappedExtension) {
        this.servletLoader = servletLoader;
        this.servletPathTranslator = servletPathTranslator;
        this.servletContainer = servletContainer;
        this.servletContext = servletContext;
        this.servletMappedExtension = servletMappedExtension;
        servletConfig = new ServletConfigWrapper(servletContext);
    }

    @Override
    public boolean canLoad(String path) {
        String servletClass = servletPathTranslator.toClassName(path);
        return isServletExtension(path) && servletLoader.canLoadServlet(servletClass);
    }

    @Override
    public void load(String path, HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {

        // TODO Handling of ServletException should be improved

        try {
            String servletClass = servletPathTranslator.toClassName(path);
            Servlet servlet = servletContainer.getForClassName(servletClass, servletConfig);

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

    private boolean isServletExtension(String uri) {
        return Utilities.getExtension(uri).equals(servletMappedExtension);
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
                servletContext.handleSession(session, response);
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
