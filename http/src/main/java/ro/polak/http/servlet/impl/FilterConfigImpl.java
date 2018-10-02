/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package ro.polak.http.servlet.impl;

import ro.polak.http.servlet.FilterConfig;
import ro.polak.http.servlet.ServletContext;

/**
 * Default config.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class FilterConfigImpl implements FilterConfig {

    private final ServletContext servletContext;

    /**
     * Default constructor.
     *
     * @param servletContext
     */
    public FilterConfigImpl(final ServletContext servletContext) {
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
