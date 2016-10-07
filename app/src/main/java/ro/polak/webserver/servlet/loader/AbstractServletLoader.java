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
 * Provides common methods for servlet service drivers.
 */
abstract public class AbstractServletLoader implements ServletLoader {

    @Override
    public Servlet loadServlet(String servletPath) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        int lastSlashPos = servletPath.lastIndexOf('/');

        // Detecting servlet name and servlet directory (package)
        // IMPORTANT! This imposes a constraint that all the servlets must be in a package
        String servletName = servletPath.substring(lastSlashPos + 1);
        String servletDir = servletPath.substring(0, lastSlashPos + 1);

        // Removing extension if needed
        int extensionSeparatorPos = servletName.lastIndexOf('.');
        if (extensionSeparatorPos > -1) {
            servletName = servletName.substring(0, extensionSeparatorPos);
        }

        // Generating class name and instantiating servlet
        servletName = servletDir.substring(1).replaceAll("/", ".") + servletName;
        return instantiateServlet(servletName);
    }

    /**
     * Returns instantiated servlet.
     *
     * @param classCanonicalName
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    abstract protected Servlet instantiateServlet(String classCanonicalName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException;
}
