/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.resource.provider;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.HttpResponseHeaders;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.error.HTTPError500;
import ro.polak.webserver.servlet.HttpRequest;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.Servlet;
import ro.polak.webserver.servlet.loader.ClassPathServletLoader;
import ro.polak.webserver.servlet.loader.ServletLoader;

/**
 * Servlet resource provider
 * <p/>
 * This provider enables the URLs to be interpreted by servlets
 *
 * @author Piotr Polak piotr [at] polak [dot] ro
 * @since 201509
 */
public class ServletResourceProvider implements ResourceProvider {

    // Initialize servlet service in a static way
    private static ServletLoader servletService;

    static {
        servletService = new ro.polak.webserver.servlet.ServletLoader(new ClassPathServletLoader());
    }

    @Override
    public boolean load(String uri, HttpRequest request, HttpResponse response) {
        // Detect the extension
        String fileExtension = Utilities.getExtension(uri);

        // Check whether the extension is of Servlet type
        if (fileExtension.equals(MainController.getInstance().getWebServer().getServerConfig().getServletMappedExtension())) {
            try {
                Servlet servlet = servletService.loadServlet(uri);
                response.setStatus(HttpResponseHeaders.STATUS_OK);
                servlet.run(request, response);
            } catch (Exception e) {
                HTTPError500 error500 = new HTTPError500();
                error500.setReason(e);
                error500.serve(response);
            } catch (Error e) {
                // For compilation problems
                HTTPError500 error500 = new HTTPError500();
                error500.setReason(e);
                error500.serve(response);
            }
            return true;
        }

        return false;
    }
}
