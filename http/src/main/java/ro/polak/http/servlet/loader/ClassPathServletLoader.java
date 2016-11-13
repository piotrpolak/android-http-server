/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet.loader;

import java.io.IOException;

import ro.polak.http.servlet.Servlet;

/**
 * Loads and rolls servlets on Android.
 * The servlet classes are loaded from the current package or classpath.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201002
 */
public class ClassPathServletLoader extends AbstractServletLoader {

    @Override
    protected Servlet instantiateServlet(String classCanonicalName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return (Servlet) Class.forName(classCanonicalName).newInstance();
    }
}
