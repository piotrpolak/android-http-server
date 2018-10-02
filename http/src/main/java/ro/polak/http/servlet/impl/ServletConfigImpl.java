/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.servlet.impl;

import ro.polak.http.servlet.ServletConfig;
import ro.polak.http.servlet.ServletContext;

/**
 * Servlet config implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class ServletConfigImpl implements ServletConfig {

    private final ServletContext servletContext;

    /**
     * Default constructor.
     *
     * @param servletContext
     */
    public ServletConfigImpl(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
}
