/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.resource.provider.impl;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.Headers;
import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.exception.FilterInitializationException;
import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.exception.UnexpectedSituationException;
import ro.polak.http.resource.provider.ResourceProvider;
import ro.polak.http.servlet.Filter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.FilterConfig;
import ro.polak.http.servlet.FilterConfigWrapper;
import ro.polak.http.servlet.HttpRequestWrapper;
import ro.polak.http.servlet.HttpResponseWrapper;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.HttpSessionWrapper;
import ro.polak.http.servlet.Servlet;
import ro.polak.http.servlet.ServletConfigWrapper;
import ro.polak.http.servlet.ServletContainer;
import ro.polak.http.servlet.ServletContext;
import ro.polak.http.servlet.ServletContextHelper;
import ro.polak.http.servlet.ServletContextWrapper;
import ro.polak.http.servlet.UploadedFile;
import ro.polak.http.servlet.impl.FilterChainImpl;

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
    private final List<ServletContextWrapper> servletContexts;
    private final ServletContextHelper servletContextHelper = new ServletContextHelper();

    /**
     * Default constructor.
     *
     * @param servletContainer
     * @param servletContexts
     */
    public ServletResourceProvider(final ServletContainer servletContainer,
                                   final List<ServletContextWrapper> servletContexts) {
        this.servletContainer = servletContainer;
        this.servletContexts = servletContexts;
    }

    @Override
    public boolean canLoad(String path) {
        ServletContext servletContext = servletContextHelper.getResolvedContext(servletContexts, path);
        return servletContext != null && servletContextHelper.getResolvedServletMapping(servletContext, path) != null;
    }

    @Override
    public void load(String path, HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {
        ServletContextWrapper servletContext = servletContextHelper.getResolvedContext(servletContexts, path);
        Objects.requireNonNull(servletContext);
        ServletMapping servletMapping = servletContextHelper.getResolvedServletMapping(servletContext, path);

        request.setServletContext(servletContext);

        Servlet servlet = getServlet(servletMapping, new ServletConfigWrapper(servletContext));

        response.setStatus(HttpServletResponse.STATUS_OK);
        try {
            FilterChainImpl filterChain = getFilterChain(path, servletContext, servlet);
            filterChain.doFilter(request, response);
            terminate(request, response);
        } catch (ServletException | FilterInitializationException e) {
            throw new UnexpectedSituationException(e);
        }
    }

    private Servlet getServlet(ServletMapping servletMapping, ServletConfigWrapper servletConfig) {
        Servlet servlet;
        try {
            servlet = servletContainer.getServletForClass(servletMapping.getServletClass(), servletConfig);
        } catch (ServletInitializationException | ServletException e) {
            throw new UnexpectedSituationException(e);
        }
        return servlet;
    }

    private FilterChainImpl getFilterChain(String path, ServletContextWrapper servletContext, final Servlet servlet)
            throws FilterInitializationException, ServletException {

        ArrayDeque<Filter> arrayDeque = new ArrayDeque<>(getFilterMappingsForPath(path, servletContext));
        arrayDeque.add(new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {

            }

            @Override
            public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
                servlet.service(request, response);
            }
        });
        return new FilterChainImpl(arrayDeque);
    }

    private List<Filter> getFilterMappingsForPath(String path, ServletContextWrapper servletContext)
            throws FilterInitializationException, ServletException {

        FilterConfig filterConfig = new FilterConfigWrapper(servletContext);

        List<Filter> filters = new ArrayList<>();
        for (FilterMapping filterMapping : servletContextHelper.getFilterMappingsForPath(servletContext, path)) {
            filters.add(servletContainer.getFilterForClass(filterMapping.getFilterClass(), filterConfig));
        }

        return filters;
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
