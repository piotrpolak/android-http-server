package ro.polak.webserver.servlet;

/**
 * Little servlet
 *
 * @author Piotr Polak <a href="http://www.polak.ro/">www.polak.ro</a>
 * @version 1.0/21.02.2008
 */
public abstract class Servlet implements ServletInterface {

    protected HTTPSession session;
    // Info
    protected String directory;

    /**
     * Runs the servlet
     *
     * @param request  request
     * @param response response
     */
    protected void run(HTTPRequest request, HTTPResponse response) {
        this.session = new HTTPSession(request, response);

        this.main(request, response);
        this.terminate(request, response);
    }

    public void initialize() {
    }

    public void finalize() {
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
     * Returns servlet directory
     *
     * @return servlet directory
     */
    public String getDirectory() {
        return directory;
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
