/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import ro.polak.webserver.controller.MainController;

public class DesktopServletServiceDriver implements IServletServiceDriver {

    private Servlet littleServlet;
    // private static ServletPool servletPool = new ServletPool();
    private static ClassLoader classLoader = null;

    static {
        try {
            DesktopServletServiceDriver.classLoader = new URLClassLoader((new URL[]{new URL("file", "", new File(MainController.getInstance().getServer().getServerConfig().getDocumentRootPath()).getCanonicalPath().replace('\\', '/') + "/")}));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

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
        servletName = servletDir.substring(1).replaceAll("/", ".") + servletName;

        // littleServlet = ServletService.servletPool.getServlet( servletName );
        //
        //
        // if( littleServlet != null )
        // {
        // return true;
        // }

        try {

            // Initializing servlet
            littleServlet = (Servlet) DesktopServletServiceDriver.classLoader.loadClass(servletName).newInstance();

            try {
                littleServlet.directory = (new File(MainController.getInstance().getServer().getServerConfig().getDocumentRootPath())).getCanonicalPath();
            } catch (java.io.IOException e) {
                littleServlet.directory = MainController.getInstance().getServer().getServerConfig().getDocumentRootPath();
            }
        } catch (ClassCastException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }

        // ServletService.servletPool.add(servletName, littleServlet);
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
