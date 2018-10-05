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
 * Filter chain interface easing running filters.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public interface FilterChain {

    /**
     * Filters use the FilterChain to invoke the next filter in the chain.
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    void doFilter(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}
