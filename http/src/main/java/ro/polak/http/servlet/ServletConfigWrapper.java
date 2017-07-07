/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.servlet;

/**
 * Servlet config implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class ServletConfigWrapper implements ServletConfig {

    private final ServletContext servletContext;

    /**
     * Default constructor.
     *
     * @param servletContext
     */
    public ServletConfigWrapper(ServletContextWrapper servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
}
