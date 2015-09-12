package ro.polak.webserver.servlet;

/**
 * Servlet v3 interface, declares service() method
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200902
 */
public interface IServlet {

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
