/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet.loader;

import java.io.IOException;

import ro.polak.webserver.servlet.Servlet;

/**
 * Servlet service interface
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public interface ServletLoader {

    /**
     * Loads requested little servlet
     *
     * @param classCanonicalName the path of the little servlet (requested URI)
     * @return true if little servlet found and loaded
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    Servlet loadServlet(String classCanonicalName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException;
}
