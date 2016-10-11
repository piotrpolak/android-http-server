/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet.loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.servlet.Servlet;

/**
 * Loads and rolls servlets on Desktop.
 * The servlet classes are loaded from disk space.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class FileSystemServletLoader extends AbstractServletLoader {

    private static ClassLoader classLoader = null;

    @Override
    protected Servlet instantiateServlet(String classCanonicalName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        if (FileSystemServletLoader.classLoader == null) {
            FileSystemServletLoader.classLoader = new URLClassLoader((new URL[]{new URL("file", "", new File(MainController.getInstance().getWebServer().getServerConfig().getDocumentRootPath()).getCanonicalPath().replace('\\', '/') + "/")}));
        }
        return (Servlet) FileSystemServletLoader.classLoader.loadClass(classCanonicalName).newInstance();
    }
}
