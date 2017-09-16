/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.servlet;

import ro.polak.http.exception.ServletException;
import ro.polak.http.exception.ServletInitializationException;

/**
 * Manages life cycle of servlets.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 20170
 */
interface ServletContainer {

    /**
     * Returns initialized servlet for given class name.
     *
     * @param servletClassName
     * @param servletConfig
     * @return
     * @throws ServletInitializationException
     * @throws ServletException
     */
    Servlet getForClassName(String servletClassName, ServletConfig servletConfig)
            throws ServletInitializationException, ServletException;
}
