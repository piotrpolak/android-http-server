/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet;

import ro.polak.http.exception.ServletException;

/**
 * Default abstract servlet.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public abstract class HttpServlet implements Servlet {

    private ServletConfig servletConfig;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ServletConfig servletConfig) throws ServletException {
        this.servletConfig = servletConfig;
        init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws ServletException {
        // To be overwritten
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Empty by default, should be overwritten by the implementing servlet
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServletInfo() {
        return "";
    }

    /**
     * Returns servlet context.
     *
     * @return
     */
    public ServletContext getServletContext() {
        if (servletConfig == null) {
            return null;
        }
        return servletConfig.getServletContext();
    }
}
