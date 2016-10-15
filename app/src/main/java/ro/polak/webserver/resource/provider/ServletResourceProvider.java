/**************************************************
 * Android Web Server
 * Based on JavaLittleWebServer (2008)
 * <p/>
 * Copyright (c) Piotr Polak 2008-2016
 **************************************************/

package ro.polak.webserver.resource.provider;

import java.io.IOException;

import ro.polak.utilities.Utilities;
import ro.polak.webserver.Headers;
import ro.polak.webserver.controller.MainController;
import ro.polak.webserver.error.HttpError500;
import ro.polak.webserver.servlet.HttpRequestWrapper;
import ro.polak.webserver.servlet.HttpResponse;
import ro.polak.webserver.servlet.HttpResponseWrapper;
import ro.polak.webserver.servlet.HttpSessionWrapper;
import ro.polak.webserver.servlet.Servlet;
import ro.polak.webserver.servlet.ServletConfigWrapper;
import ro.polak.webserver.servlet.ServletContextWrapper;
import ro.polak.webserver.servlet.loader.ClassPathServletLoader;
import ro.polak.webserver.servlet.loader.ServletLoader;
import ro.polak.webserver.session.storage.FileSessionStorage;

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
    private static ServletContextWrapper servletContext;

    static {
        String tmpPath = MainController.getInstance().getWebServer().getServerConfig().getTempPath();
        servletService = new ro.polak.webserver.servlet.ServletLoader(new ClassPathServletLoader());
        servletContext = new ServletContextWrapper(new FileSessionStorage(tmpPath));
    }

    @Override
    public boolean load(String uri, HttpRequestWrapper request, HttpResponseWrapper response) {

        String extension = Utilities.getExtension(uri);

        // Check whether the extension is of Servlet type
        if (extension.equals(MainController.getInstance().getWebServer().getServerConfig().getServletMappedExtension())) {
            try {
                Servlet servlet;
                try {
                    servlet = servletService.loadServlet(uri);
                } catch (ClassNotFoundException e) {
                    return false;
                }

                ServletConfigWrapper servletConfig = new ServletConfigWrapper();
                request.setServletContext(servletContext);
                servletConfig.setServletContext(servletContext);

                servlet.init(servletConfig);
                response.setStatus(HttpResponse.STATUS_OK);
                servlet.service(request, response);
                terminate(request, response);
            } catch (Exception e) {
                HttpError500 error500 = new HttpError500();
                error500.setReason(e);
                error500.serve(response);
                e.printStackTrace();
            } catch (Error e) {
                // For compilation problems
                HttpError500 error500 = new HttpError500();
                error500.setReason(e);
                error500.serve(response);
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    /**
     * Terminates servlet. Sets all necessary headers, flushes content.
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void terminate(HttpRequestWrapper request, HttpResponseWrapper response) throws IOException {
        request.getFileUpload().freeResources();

        HttpSessionWrapper session = request.getSession(false);
        if (session != null) {
            try {
                servletContext.handleSession(session, response);
            } catch (IOException e) {
                MainController.getInstance().println(getClass(), "Unable to persist session: " + e.getMessage());
            }
        }

        if (!response.isCommitted()) {
            if (response.getContentType() == null) {
                response.setContentType("text/html");
            }

            if (response.getPrintWriter().isInitialized()) {
                if (!response.getHeaders().containsHeader(Headers.HEADER_CONTENT_LENGTH)) {
                    response.setContentLength(response.getPrintWriter().length());
                }
            }

            response.getHeaders().setHeader(Headers.HEADER_CACHE_CONTROL, "no-cache");
            response.getHeaders().setHeader(Headers.HEADER_PRAGMA, "no-cache");

            response.flushHeaders();
        }

        if (response.getPrintWriter().length() > 0) {
            response.write(response.getPrintWriter().toString());
        }
    }
}
