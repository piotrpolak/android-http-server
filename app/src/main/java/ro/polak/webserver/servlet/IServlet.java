package ro.polak.webserver.servlet;

/**
 * Servlet v2 interface, declares main() method
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 2.0/02.02.2009
 */
public interface IServlet {

    // TODO Rename according the real Servlet method names

    /**
     * The servlet initialization method. The reusable resources should be
     * initialized in the init method
     */
    void init();

    /**
     * The servlet destroy method. The reusable resources should be destoroyed
     * in the destroy method
     */
    void destroy();

    /**
     * The main method of the servlet. Must be overridden, contains the servlet
     * body.
     */
    void service(HTTPRequest request, HTTPResponse response);
}
