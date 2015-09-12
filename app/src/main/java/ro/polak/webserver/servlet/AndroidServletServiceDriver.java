package ro.polak.webserver.servlet;

import android.util.Log;

public class AndroidServletServiceDriver implements IServletServiceDriver {

    private Servlet littleServlet;

    /**
     * Loads requested little servlet
     *
     * @param servletPath the path of the little servlet (requested URI)
     * @return true if little servlet found and loaded
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassCastException
     */
    public boolean loadServlet(String servletPath) throws InstantiationException, IllegalAccessException, ClassCastException {

        int lastSlashPos = 0;

        try {
            lastSlashPos = servletPath.lastIndexOf("/");
        } catch (Exception e) {
        }

        String servletName = servletPath.substring(lastSlashPos + 1);
        String servletDir = servletPath.substring(0, lastSlashPos + 1);

        try {
            servletName = servletName.substring(0, servletName.indexOf("."));
        } catch (Exception e) {
        }

        servletName = servletName.substring(0, 1).toUpperCase() + servletName.substring(1);

        servletName = servletDir.substring(1).replaceAll("/", ".") + servletName;

        try {
            littleServlet = (Servlet) Class.forName(servletName).newInstance();
        } catch (Exception e) {
            Log.i("SERVLET", "Unable to load servlet at " + servletName);
            return false;
        }

        // ServletService.servletPool.add(servletName, littleServlet);
        littleServlet.init();
        return true;
    }

    /**
     * Runs (starts) servlet
     *
     * @param request  http request
     * @param response http response
     */
    public void rollServlet(HTTPRequest request, HTTPResponse response) {
        if (littleServlet == null) {
            return;
        }

        littleServlet.run(request, response);
        littleServlet = null;

        // Calling garbage collector
        System.gc();
        System.gc();
    }
}
