/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2016-2016
 **************************************************/

package ro.polak.http.servlet;

/**
 * Servlet config.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
public interface ServletConfig {

    /**
     * Returns servlet context.
     *
     * @return
     */
    ServletContext getServletContext();
}
