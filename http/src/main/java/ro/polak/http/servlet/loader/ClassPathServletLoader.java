/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet.loader;

import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.servlet.HttpServlet;

/**
 * The servlet classes are loaded from the current package or classpath.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201002
 */
public class ClassPathServletLoader implements ServletLoader {

    @Override
    public boolean canLoadServlet(String classCanonicalName) {
        try {
            Class.forName(classCanonicalName);
        } catch (ClassNotFoundException e) {
            return false;
        }

        return true;
    }

    @Override
    public HttpServlet loadServlet(String classCanonicalName) throws ServletInitializationException {
        try {
            return (HttpServlet) Class.forName(classCanonicalName).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new ServletInitializationException(e);
        }
    }
}
