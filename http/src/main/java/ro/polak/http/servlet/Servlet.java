/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet;

import ro.polak.http.exception.ServletException;

/**
 * HttpServlet v3 interface, declares service() method.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200902
 */
public interface Servlet {

    /**
     * Initialization method that can be overwritten.
     *
     * @throws ServletException
     */
    void init() throws ServletException;

    /**
     * The servlet initialization method. The reusable resources should be
     * initialized in the init method.
     *
     * @param servletConfig
     */
    void init(ServletConfig servletConfig) throws ServletException;

    /**
     * Called by the container to indicate to a servlet that is is going to be taken out of service.
     */
    void destroy();

    /**
     * Called by servlet container, the main servlet logic method.
     */
    void service(HttpServletRequest request, HttpServletResponse response) throws ServletException; /* IOException */

    /**
     * Returns servlet info such as author or copyright.
     *
     * @return
     */
    String getServletInfo();
}
