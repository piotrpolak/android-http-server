/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2018-2018
 **************************************************/

package ro.polak.http.configuration;

import java.util.regex.Pattern;

import ro.polak.http.servlet.HttpServlet;

/**
 * Represents Servlet to URL patter mapping.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201803
 */
public interface ServletMapping {

    /**
     * Returns registration URL pattern.
     *
     * @return
     */
    Pattern getUrlPattern();

    /**
     * Returns mapped servlet class.
     *
     * @return
     */
    Class<? extends HttpServlet> getServletClass();
}
