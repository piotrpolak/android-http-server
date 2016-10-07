/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

import java.io.IOException;

/**
 * Little servlet service for loading and rolling servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class ServletLoader implements ro.polak.webserver.servlet.loader.ServletLoader {

    private ro.polak.webserver.servlet.loader.ServletLoader driver;

    /**
     * Default constructor
     *
     * @param driver
     */
    public ServletLoader(ro.polak.webserver.servlet.loader.ServletLoader driver) {
        this.driver = driver;
    }

    @Override
    public Servlet loadServlet(String servletPath) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
        return driver.loadServlet(servletPath);
    }
}
