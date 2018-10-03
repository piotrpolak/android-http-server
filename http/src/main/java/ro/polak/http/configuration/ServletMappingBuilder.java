/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.configuration;

import java.util.regex.Pattern;

import ro.polak.http.configuration.impl.ServletMappingImpl;
import ro.polak.http.servlet.HttpServlet;

/**
 * Utility for building servlet mapping configuration.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public final class ServletMappingBuilder {

    private final ServletContextBuilder servletContextBuilder;
    private Pattern urlPattern;
    private Class<? extends HttpServlet> servletClass;

    /**
     * Created a mapping builder. This constructor should be package scoped.
     *
     * @param servletContextBuilder
     */
    ServletMappingBuilder(final ServletContextBuilder servletContextBuilder) {
        this.servletContextBuilder = servletContextBuilder;
    }

    public ServletMappingBuilder withUrlPattern(final Pattern urlPattern) {
        this.urlPattern = urlPattern;
        return this;
    }

    public ServletMappingBuilder withServletClass(final Class<? extends HttpServlet> servletClass) {
        this.servletClass = servletClass;
        return this;
    }

    public ServletContextBuilder end() {
        servletContextBuilder.withServletMapping(new ServletMappingImpl(urlPattern, servletClass));
        return servletContextBuilder;
    }
}
