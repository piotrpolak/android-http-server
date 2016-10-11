/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.webserver.servlet;

/**
 * Servlet config implementation.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public class ServletConfigWrapper implements ServletConfig {

    private ServletContext servletContext;

    /**
     * Sets the servlet context.
     *
     * @param servletContext
     */
    public void setServletContext(ServletContextWrapper servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
}
