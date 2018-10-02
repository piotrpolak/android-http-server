/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.configuration.impl;

import java.util.regex.Pattern;

import ro.polak.http.configuration.ServletMapping;
import ro.polak.http.servlet.HttpServlet;

/**
 * Default implementation for ServletMapping.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class ServletMappingImpl implements ServletMapping {

    private final Pattern urlPattern;

    private final Class<? extends HttpServlet> servletClass;

    public ServletMappingImpl(final Pattern urlPattern, final Class<? extends HttpServlet> servletClass) {
        this.urlPattern = urlPattern;
        this.servletClass = servletClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pattern getUrlPattern() {
        return urlPattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends HttpServlet> getServletClass() {
        return servletClass;
    }
}
