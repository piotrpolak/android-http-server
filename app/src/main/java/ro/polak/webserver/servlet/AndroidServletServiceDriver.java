/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

public class AndroidServletServiceDriver implements IServletServiceDriver {

    private Servlet littleServlet;

    /**
     * Loads requested little servlet
     *
     * @param servletPath the path of the little servlet (requested URI)
     * @return true if little servlet found and loaded
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassCastException
     */
    public boolean loadServlet(String servletPath) throws InstantiationException, IllegalAccessException, ClassCastException {

        // Finding last occurrence of /
        int lastSlashPos = 0;
        try {
            lastSlashPos = servletPath.lastIndexOf("/");
        } catch (Exception e) {
        }

        // Detecting servlet name and servlet directory (package)
        // IMPORTANT! This imposes a constraint that all the servlets must be in a package
        String servletName = servletPath.substring(lastSlashPos + 1);
        String servletDir = servletPath.substring(0, lastSlashPos + 1);

        try {
            // Removing extension if needed
            servletName = servletName.substring(0, servletName.indexOf("."));
        } catch (Exception e) {
        }

        // Generating class name
        servletName = servletName.substring(0, 1).toUpperCase() + servletName.substring(1);

        // Prepending directory name
        servletName = servletDir.substring(1).replaceAll("/", ".") + servletName;

        try {
            // Initializing servlet
            littleServlet = (Servlet) Class.forName(servletName).newInstance();
        } catch (Exception e) {
            return false;
        }

        // The following code has been disabled on Android for memory usage
        // ServletService.servletPool.add(servletName, littleServlet);

        // Initializing the servlet
        littleServlet.init();
        return true;
    }

    /**
     * Runs (starts) servlet
     *
     * @param request  http request
     * @param response http response
     */
    public void rollServlet(HTTPRequest request, HTTPResponse response) {

        // Make suer it was initialized before
        if (littleServlet == null) {
            return;
        }

        // Running, then removing the servlet
        littleServlet.run(request, response);
        littleServlet = null;

        // Calling garbage collector
        System.gc();
        System.gc();
    }
}
