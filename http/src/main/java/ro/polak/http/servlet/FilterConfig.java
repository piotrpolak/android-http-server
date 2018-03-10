/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2018
 **************************************************/

package ro.polak.http.servlet;

/**
 * Filter config.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public interface FilterConfig {

    /**
     * Returns associated servlet context.
     *
     * @return
     */
    ServletContext getServletContext();
}
