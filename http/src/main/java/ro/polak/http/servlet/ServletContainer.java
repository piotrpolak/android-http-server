/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.servlet;

import ro.polak.http.exception.FilterInitializationException;
import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;

/**
 * Manages life cycle of servlets.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 20170
 */
public interface ServletContainer {

    /**
     * Returns initialized servlet for given class name.
     *
     * @param servletClass
     * @param servletConfig
     * @return
     * @throws ServletInitializationException
     * @throws ServletException
     */
    Servlet getServletForClass(Class<? extends HttpServlet> servletClass, ServletConfig servletConfig)
            throws ServletInitializationException, ServletException;

    /**
     * Returns initialized servlet for given class name.
     *
     * @param filterClass
     * @return
     * @throws FilterInitializationException
     */
    Filter getFilterForClass(Class<? extends Filter> filterClass, FilterConfig filterConfig)
            throws FilterInitializationException, ServletException;

    /**
     * Shuts down the servlet container, closes all open resources.
     */
    void shutdown();
}
