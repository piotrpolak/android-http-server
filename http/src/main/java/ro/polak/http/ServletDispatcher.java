/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ro.polak.http.configuration.FilterMapping;
import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.exception.FilterInitializationException;
import ro.polak.http.exception.NotFoundException;
import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.exception.UnexpectedSituationException;
import ro.polak.http.servlet.BasicAbstractFilter;
import ro.polak.http.servlet.Filter;
import ro.polak.http.servlet.FilterChain;
import ro.polak.http.servlet.FilterConfig;
import ro.polak.http.servlet.HttpServletRequest;
import ro.polak.http.servlet.HttpServletResponse;
import ro.polak.http.servlet.Servlet;
import ro.polak.http.servlet.ServletContainer;
import ro.polak.http.servlet.UploadedFile;
import ro.polak.http.servlet.helper.ServletContextHelper;
import ro.polak.http.servlet.impl.FilterChainImpl;
import ro.polak.http.servlet.impl.FilterConfigImpl;
import ro.polak.http.servlet.impl.HttpServletRequestImpl;
import ro.polak.http.servlet.impl.HttpServletResponseImpl;
import ro.polak.http.servlet.impl.HttpSessionImpl;
import ro.polak.http.servlet.impl.ServletConfigImpl;
import ro.polak.http.servlet.impl.ServletContextImpl;

/**
 * Servlet resource provider.
 * <p/>
 * This provider enables the URLs to be interpreted by servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class ServletDispatcher implements Loadable {

    private static final Logger LOGGER = Logger.getLogger(ServletDispatcher.class.getName());
    private static final String DEFAULT_RESPONSE_CONTENT_TYPE = "text/html";
    private static final String HEADER_VALUE_NO_CACHE = "no-cache";

    private final ServletContainer servletContainer;
    private final List<ServletContextImpl> servletContexts;
    private final ServletContextHelper servletContextHelper = new ServletContextHelper();

    /**
     * Default constructor.
     *
     * @param servletContainer
     * @param servletContexts
     */
    public ServletDispatcher(final ServletContainer servletContainer,
                             final List<ServletContextImpl> servletContexts) {
        this.servletContainer = servletContainer;
        this.servletContexts = servletContexts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(final String path,
                     final HttpServletRequestImpl request,
                     final HttpServletResponseImpl response) throws IOException {
        ServletContextImpl servletContext = servletContextHelper.getResolvedContext(servletContexts, path);
        if (servletContext == null) {
            throw new NotFoundException("No servlet context resolved.");
        }
        ServletMapping servletMapping = servletContextHelper.getResolvedServletMapping(servletContext, path);

        request.setServletContext(servletContext);

        try {
            FilterChainImpl filterChain;
            if (servletMapping != null) {
                Servlet servlet = getServlet(servletMapping, new ServletConfigImpl(servletContext));
                response.setStatus(HttpServletResponse.STATUS_OK);
                filterChain = getFilterChain(path, servletContext, servlet);
            } else {
                filterChain = getFilterChain(path, servletContext, null);
            }

            filterChain.doFilter(request, response);
            terminate(request, response);
        } catch (ServletException | FilterInitializationException e) {
            throw new UnexpectedSituationException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        servletContainer.shutdown();
    }

    private Servlet getServlet(final ServletMapping servletMapping, final ServletConfigImpl servletConfig) {
        Servlet servlet;
        try {
            servlet = servletContainer.getServletForClass(servletMapping.getServletClass(), servletConfig);
        } catch (ServletInitializationException | ServletException e) {
            throw new UnexpectedSituationException(e);
        }
        return servlet;
    }

    private FilterChainImpl getFilterChain(final String path, final ServletContextImpl servletContext, final Servlet servlet)
            throws FilterInitializationException, ServletException {

        Deque<Filter> deque = new ArrayDeque<>(getFilterMappingsForPath(path, servletContext));

        if (servlet != null) {
            deque.add(new BasicAbstractFilter() {
                @Override
                public void doFilter(final HttpServletRequest request,
                                     final HttpServletResponse response,
                                     final FilterChain filterChain) throws ServletException {
                    servlet.service(request, response);
                }
            });
        }
        if (deque.isEmpty()) {
            throw new NotFoundException();
        }

        return new FilterChainImpl(deque);
    }

    private List<Filter> getFilterMappingsForPath(final String path, final ServletContextImpl servletContext)
            throws FilterInitializationException, ServletException {

        FilterConfig filterConfig = new FilterConfigImpl(servletContext);

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
    private void terminate(final HttpServletRequestImpl request, final HttpServletResponseImpl response) throws IOException {
        freeUploadedUnprocessedFiles(request.getUploadedFiles());

        HttpSessionImpl session = (HttpSessionImpl) request.getSession(false);
        if (session != null) {
            try {
                ((ServletContextImpl) request.getServletContext()).handleSession(session, response);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Unable to persist session", e);
            }
        }

        if (!response.isCommitted()) {
            if (response.getContentType() == null) {
                response.setContentType(DEFAULT_RESPONSE_CONTENT_TYPE);
            }

            response.getHeaders().setHeader(Headers.HEADER_CACHE_CONTROL, HEADER_VALUE_NO_CACHE);
            response.getHeaders().setHeader(Headers.HEADER_PRAGMA, HEADER_VALUE_NO_CACHE);
        }

        response.flush();
    }

    private void freeUploadedUnprocessedFiles(final Collection<UploadedFile> uploadedFiles) {
        for (UploadedFile uploadedFile : uploadedFiles) {
            uploadedFile.destroy();
        }
    }
}
