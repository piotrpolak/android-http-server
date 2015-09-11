package ro.polak.webserver.servlet;

public interface IServletServiceDriver {

    /**
     * Loads requested little servlet
     *
     * @param servletPath the path of the little servlet (requested URI)
     * @return true if little servlet found and loaded
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassCastException
     */
    boolean loadServlet(String servletPath) throws InstantiationException, IllegalAccessException, ClassCastException;

    /**
     * Runs (starts) servlet
     *
     * @param request  http request
     * @param response http response
     */
    void rollServlet(HTTPRequest request, HTTPResponse response);
}
