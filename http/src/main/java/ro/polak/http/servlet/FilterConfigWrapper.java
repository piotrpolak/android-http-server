/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package ro.polak.http.servlet;

/**
 * Default config.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public class FilterConfigWrapper implements FilterConfig {

    private final ServletContext servletContext;

    /**
     * Default constructor.
     *
     * @param servletContext
     */
    public FilterConfigWrapper(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
}
