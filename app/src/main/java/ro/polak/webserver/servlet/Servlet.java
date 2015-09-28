/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.servlet;

/**
 * Servlet
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @version 201509
 * @since 200802
 */
public abstract class Servlet implements IServlet {

    private HTTPSession session;

    /**
     * Runs the servlet
     *
     * @param request  request
     * @param response response
     */
    protected void run(HTTPRequest request, HTTPResponse response) {
        this.session = new HTTPSession(request, response);

        this.service(request, response);
        this.terminate(request, response);
    }

    /**
     * The servlet initialization method. The reusable resources should be
     * initialized in the init method
     */
    public void init() {
    }

    /**
     * The servlet destroy method. The reusable resources should be destoroyed
     * in the destroy method
     */
    public void destroy() {
    }

    /**
     * Terminates servlet
     * <p/>
     * Sets all necessary headers, flushes content
     */
    private void terminate(HTTPRequest request, HTTPResponse response) {
        // Freezing session
        session.freeze();

        // Releasing resources
        request.getFileUpload().freeResources();

        // Setting and flushing headers
        response.setContentType("text/html");

        if (response.getPrintWriter().initialized) {
            response.setContentLength(response.getPrintWriter().length());
        }

        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");

        response.flushHeaders();

        // Setting and flushing contents
        response.write(response.getPrintWriter().toString());

        // response.getPrintWriter().writeToResponse(response);

        try {
            response.flush();
        } catch (Exception e) {
        }
    }

    /**
     * Returns HTTP session
     *
     * @return
     */
    public HTTPSession getSession() {
        return session;
    }

    /**
     * Returns servlet class name
     *
     * @return servlet class name
     */
    public String getName() {
        return this.getClass().getName();
    }

    /**
     * Returns servlet path
     *
     * @return servlet path
     */
    public String getPath() {
        // TODO This is not valid for the Android paths
        return this.getDirectory() + java.io.File.separator + this.getName() + ".class";
    }

    /**
     * Returns a clone
     *
     * @return
     */
    public Servlet getClone() {
        try {
            return (Servlet) this.clone();
        } catch (Exception e) {
        }

        return null;
    }
}
