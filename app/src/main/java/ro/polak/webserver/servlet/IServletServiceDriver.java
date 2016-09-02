/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

/**
 * Servlet service interface
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public interface IServletServiceDriver {

    /**
     * Loads requested little servlet
     *
     * @param servletPath the path of the little servlet (requested URI)
     * @return true if little servlet found and loaded
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassCastException
     */
    boolean loadServlet(String servletPath) throws InstantiationException, IllegalAccessException, ClassCastException;

    /**
     * Runs (starts) servlet
     *
     * @param request  http request
     * @param response http response
     */
    void rollServlet(HTTPRequest request, HTTPResponse response);
}
