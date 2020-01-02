/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package ro.polak.http.servlet;

import java.io.IOException;

import ro.polak.http.exception.ServletException;

/**
 * Filter interface.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public interface Filter {

    /**
     * Initializes filter.
     *
     * @param filterConfig
     * @throws ServletException
     */
    void init(FilterConfig filterConfig) throws ServletException;

    /**
     * Performs request/response filtering.
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException;


}
