/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2017
 **************************************************/

package ro.polak.http.servlet.loader;

import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.servlet.Servlet;

/**
 * Servlet service interface
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public interface ServletLoader {


    /**
     * Returns true if the given servlet loader can load servlet for the given parameter
     *
     * @param classCanonicalName
     * @return
     */
    boolean canLoadServlet(String classCanonicalName);

    /**
     * Loads requested little servlet
     *
     * @param classCanonicalName the path of the little servlet (requested URI)
     * @return Servlet instance
     * @throws ServletInitializationException
     */
    Servlet loadServlet(String classCanonicalName) throws ServletInitializationException;
}
