/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.http.servlet;

/**
 * Default abstract servlet.
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public abstract class Servlet implements HttpServlet {

    private ServletConfig servletConfig;

    @Override
    public void init(ServletConfig servletConfig) {
        this.servletConfig = servletConfig;
    }

    @Override
    public void destroy() {
        // To be implemented
    }

    /**
     * Returns servlet context.
     *
     * @return
     */
    public ServletContext getServletContext() {
        return servletConfig.getServletContext();
    }
}
