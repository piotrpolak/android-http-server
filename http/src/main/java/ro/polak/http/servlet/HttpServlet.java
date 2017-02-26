/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet;

/**
 * Servlet v3 interface, declares service() method
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200902
 */
public interface HttpServlet {

    /**
     * The servlet initialization method. The reusable resources should be
     * initialized in the init method.
     *
     * @param servletConfig
     */
    void init(ServletConfig servletConfig);

    /**
     * The main method of the servlet. Must be overridden, contains the servlet
     * body.
     */
    void service(HttpRequest request, HttpResponse response);
}
