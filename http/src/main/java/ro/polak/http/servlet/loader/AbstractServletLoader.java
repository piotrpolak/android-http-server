/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet.loader;

import ro.polak.http.exception.ServletInitializationException;
import ro.polak.http.servlet.Servlet;

/**
 * Provides common methods for servlet service drivers.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201610
 */
abstract public class AbstractServletLoader implements ServletLoader {


    @Override
    public boolean canLoadServlet(String servletPath) {
        return servletExists(getServletCanonicalName(servletPath));
    }

    @Override
    public Servlet loadServlet(String servletPath) throws ServletInitializationException {
        return instantiateServlet(getServletCanonicalName(servletPath));
    }

    private String getServletCanonicalName(String servletPath) {
        int lastSlashPos = servletPath.lastIndexOf('/');

        // Detecting servlet name and servlet directory (package)
        // IMPORTANT! This imposes a constraint that all the servlets must be in a package
        String classCanonicalName = servletPath.substring(lastSlashPos + 1);
        String servletDir = servletPath.substring(0, lastSlashPos + 1);

        // Removing extension if needed
        int extensionSeparatorPos = classCanonicalName.lastIndexOf('.');
        if (extensionSeparatorPos > -1) {
            classCanonicalName = classCanonicalName.substring(0, extensionSeparatorPos);
        }

        // Generating class name and instantiating servlet
        classCanonicalName = servletDir.substring(1).replaceAll("/", ".") + classCanonicalName;
        return classCanonicalName;
    }

    /**
     * Tells whether servlet for the given parameter exists.
     *
     * @param classCanonicalName
     * @return
     */
    abstract protected boolean servletExists(String classCanonicalName);

    /**
     * Returns instantiated servlet.
     *
     * @param classCanonicalName
     * @return
     * @throws ServletInitializationException
     */
    abstract protected Servlet instantiateServlet(String classCanonicalName) throws ServletInitializationException;
}
