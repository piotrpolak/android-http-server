/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.servlet;

import ro.polak.webserver.Headers;

/**
 * Servlet
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 200802
 */
public abstract class Servlet implements HttpServlet {

    private HttpSession session;

    /**
     * Runs the servlet
     *
     * @param request  request
     * @param response response
     */
    public void run(HttpRequest request, HttpResponse response) {
        this.session = new HttpSession(request, response);

        this.service(request, response);
        this.terminate(request, response);
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    /**
     * Terminates servlet
     * <p/>
     * Sets all necessary headers, flushes content
     */
    private void terminate(HttpRequest request, HttpResponse response) {
        // Freezing session
        session.freeze();

        // Releasing resources
        request.getFileUpload().freeResources();

        if (!response.isCommitted()) {

            // Setting and flushing headers
            if (response.getContentType() == null) {
                response.setContentType("text/html");
            }

            if (response.getPrintWriter().isInitialized()) {
                response.setContentLength(response.getPrintWriter().length());
            }

            response.getHeaders().setHeader(Headers.HEADER_CACHE_CONTROL, "no-cache");
            response.getHeaders().setHeader(Headers.HEADER_PRAGMA, "no-cache");

            response.flushHeaders();
        }


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
    public HttpSession getSession() {
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
