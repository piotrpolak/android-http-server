/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.resource.provider.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.Headers;
import ro.polak.http.error.HttpError500;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponse;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpSessionWrapper;
import ro.polak.http.servlet.Servlet;
import ro.polak.http.servlet.ServletConfigWrapper;
import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.servlet.loader.ClassPathServletLoader;
import ro.polak.http.servlet.loader.ServletLoader;
import ro.polak.utilities.Utilities;

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

    private static ServletLoader servletLoader = new ClassPathServletLoader();
    private ServletContextWrapper servletContext;
    private String servletMappedExtension;

    /**
     * Default constructor.
     *
     * @param servletContext
     * @param servletMappedExtension
     */
    public ServletResourceProvider(ServletContextWrapper servletContext, String servletMappedExtension) {
        this.servletContext = servletContext;
        this.servletMappedExtension = servletMappedExtension;
    }

    @Override
    public boolean load(String uri, HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {

        String extension = Utilities.getExtension(uri);

        // Check whether the extension is of Servlet type
        if (extension.equals(servletMappedExtension)) {
            try {
                Servlet servlet;
                try {
                    servlet = servletLoader.loadServlet(uri);
                } catch (ClassNotFoundException e) {
                    return false;
                }

                ServletConfigWrapper servletConfig = new ServletConfigWrapper();
                request.setServletContext(servletContext);
                servletConfig.setServletContext(servletContext);

                servlet.init(servletConfig);
                response.setStatus(HttpResponse.STATUS_OK);
                servlet.service(request, response);
                terminate(request, response);
            } catch (Exception e) {
                HttpError500 error500 = new HttpError500();
                error500.setReason(e);
                error500.serve(response);
                LOGGER.log(Level.SEVERE, "Servlet exception", e);
            } catch (Error e) {
                // For compilation problems
                HttpError500 error500 = new HttpError500();
                error500.setReason(e);
                error500.serve(response);
                LOGGER.log(Level.SEVERE, "Servlet exception", e);
            }

            return true;
        }

        return false;
    }

    /**
     * Terminates servlet. Sets all necessary headers, flushes content.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void terminate(HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {
        request.getFileUpload().freeResources();

        HttpSessionWrapper session = request.getSession(false);
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
}
