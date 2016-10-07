/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2015
 **************************************************/

package ro.polak.webserver.resourceloader;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.HTTPResponseHeaders;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.error.HTTPError500;
import ro.polak.webserver.servlet.AndroidServletServiceDriver;
import ro.polak.webserver.servlet.HTTPRequest;
import ro.polak.webserver.servlet.HTTPResponse;
import ro.polak.webserver.servlet.ServletService;

/**
 * Servlet resource loader
 *
 * This loader enables the URLs to be interpreted by servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class ServletResourceLoader implements IResourceLoader {

    // Initialize servlet service in a static way
    private static ServletService servletService;
    static {
        servletService = new ServletService(new AndroidServletServiceDriver());
    }

    @Override
    public boolean load(String uri, HTTPRequest request, HTTPResponse response) {

        // Detect the extension
        String fileExtension = Utilities.getExtension(uri);

        // Check whether the extension is of Servlet type
        if (fileExtension.equals(MainController.getInstance().getWebServer().getServerConfig().getServletMappedExtension())) {

            // Checking for a class
            try {
                if (servletService.loadServlet(uri)) {
                    // Servlet found and loaded
                    response.setStatus(HTTPResponseHeaders.STATUS_OK);
                    servletService.rollServlet(request, response);
                    return true;
                }
            } catch (Exception e) {
                // For servlet uncaught exceptions
                HTTPError500 error500 = new HTTPError500();
                error500.setReason(e);
                error500.serve(response);
                return true;
            } catch (Error e) {
                // For compilation problems
                HTTPError500 error500 = new HTTPError500();
                error500.setReason(e);
                error500.serve(response);
                return true;
            }
        }

        return false;
    }
}
