/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

/**
 * Little servlet service for loading and rolling servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public class ServletService implements IServletServiceDriver {

    private IServletServiceDriver driver;

    /**
     * Default constructor
     *
     * @param driver
     */
    public ServletService(IServletServiceDriver driver) {
        this.driver = driver;
    }

    @Override
    public boolean loadServlet(String servletPath) throws InstantiationException, IllegalAccessException, ClassCastException {
        return driver.loadServlet(servletPath);
    }

    @Override
    public void rollServlet(HTTPRequest request, HTTPResponse response) {
        driver.rollServlet(request, response);
    }
}
