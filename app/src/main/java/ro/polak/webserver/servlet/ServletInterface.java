package ro.polak.webserver.servlet;

/**
 * Servlet v2 interface, declares main() method
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 2.0/02.02.2009
 */
public interface ServletInterface {

    /**
     * The servlet initialization method. The reusable resources should be
     * initialized in the initialize method
     */
    public void initialize();

    /**
     * The servlet destroy method. The reusable resources should be destoroyed
     * in the finalize method
     */
    public void finalize();

    /**
     * The main method of the servlet. Must be overridden, contains the servlet
     * body.
     */
    public void main(HTTPRequest request, HTTPResponse response);
}
